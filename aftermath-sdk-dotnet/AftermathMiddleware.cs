using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;

namespace Aftermath.Sdk
{
    public class AftermathMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly string _collectorUrl;
        private readonly string _serviceName;
        private static readonly HttpClient HttpClient = new HttpClient { Timeout = TimeSpan.FromSeconds(2) };

        public AftermathMiddleware(RequestDelegate next, string collectorUrl = "http://localhost:8090/api/v1/incidents", string serviceName = "dotnet-service")
        {
            _next = next;
            _collectorUrl = collectorUrl;
            _serviceName = serviceName;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (Exception ex)
            {
                CaptureIncidentAsync(context, ex);
                throw; // Re-throw after capturing for fail-open guarantee
            }
        }

        private void CaptureIncidentAsync(HttpContext context, Exception ex)
        {
            Task.Run(async () =>
            {
                try
                {
                    var headers = new Dictionary<string, string>();
                    foreach (var h in context.Request.Headers)
                    {
                        var keyLower = h.Key.ToLower();
                        if (keyLower == "authorization" || keyLower == "cookie" || keyLower == "x-api-key")
                            headers[h.Key] = "[REDACTED]";
                        else
                            headers[h.Key] = h.Value.ToString();
                    }

                    var eventData = new
                    {
                        incidentId = Guid.NewGuid().ToString(),
                        traceId = context.Request.Headers["X-Trace-Id"].ToString() ?? Guid.NewGuid().ToString(),
                        timestamp = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds(),
                        request = new
                        {
                            method = context.Request.Method,
                            uri = context.Request.Path.ToString(),
                            headers = headers,
                            body = "",
                            timestamp = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()
                        },
                        error = new
                        {
                            exceptionClass = ex.GetType().FullName,
                            message = ex.Message,
                            stackTrace = ex.StackTrace,
                            statusCode = context.Response.StatusCode >= 400 ? context.Response.StatusCode : 500
                        },
                        deployment = new
                        {
                            serviceName = _serviceName,
                            serviceVersion = "1.0.0",
                            environment = "production",
                            commitHash = "dotnet-commit"
                        }
                    };

                    var json = JsonSerializer.Serialize(eventData);
                    var content = new StringContent(json, Encoding.UTF8, "application/json");
                    await HttpClient.PostAsync(_collectorUrl, content);
                }
                catch
                {
                    // Fail-open protection
                }
            });
        }
    }
}
