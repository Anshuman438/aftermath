require 'json'
require 'net/http'
require 'uri'
require 'securerandom'

module Aftermath
  class Middleware
    def initialize(app, options = {})
      @app = app
      @collector_url = options[:collector_url] || 'http://localhost:8090/api/v1/incidents'
      @service_name = options[:service_name] || 'ruby-service'
    end

    def call(env)
      begin
        @app.call(env)
      rescue => exception
        capture_incident_async(env, exception)
        raise exception # Re-throw after capturing
      end
    end

    private

    def capture_incident_async(env, exception)
      Thread.new do
        begin
          headers = {}
          sensitive = ['authorization', 'cookie', 'x-api-key']
          env.each do |k, v|
            if k.start_with?('HTTP_')
              header_name = k[5..-1].split('_').map(&:capitalize).join('-')
              if sensitive.include?(header_name.downcase)
                headers[header_name] = '[REDACTED]'
              else
                headers[header_name] = v.to_s
              end
            end
          end

          event = {
            incidentId: SecureRandom.uuid,
            traceId: env['HTTP_X_TRACE_ID'] || SecureRandom.uuid,
            timestamp: (Time.now.to_f * 1000).to_i,
            request: {
              method: env['REQUEST_METHOD'] || 'GET',
              uri: env['REQUEST_URI'] || '/',
              headers: headers,
              body: '',
              timestamp: (Time.now.to_f * 1000).to_i
            },
            error: {
              exceptionClass: exception.class.name,
              message: exception.message,
              stackTrace: exception.backtrace ? exception.backtrace.join("\n") : '',
              statusCode: 500
            },
            deployment: {
              serviceName: @service_name,
              serviceVersion: '1.0.0',
              environment: 'production',
              commitHash: 'ruby-commit'
            }
          }

          uri = URI.parse(@collector_url)
          http = Net::HTTP.new(uri.host, uri.port)
          http.open_timeout = 2
          http.read_timeout = 2
          req = Net::HTTP::Post.new(uri.request_uri, { 'Content-Type' => 'application/json' })
          req.body = event.to_json
          http.request(req)
        rescue => e
          # Fail-open protection
        end
      end
    end
  end
end
