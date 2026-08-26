<?php

namespace Aftermath\Sdk;

class AftermathMiddleware {
    private string $collectorUrl;
    private string $serviceName;

    public function __construct(string $collectorUrl = "http://localhost:8090/api/v1/incidents", string $serviceName = "php-service") {
        $this->collectorUrl = $collectorUrl;
        $this->serviceName = $serviceName;
    }

    public function handle(\Throwable $exception, array $serverParams = []): void {
        try {
            $sensitive = ["authorization", "cookie", "x-api-key", "http_authorization", "http_cookie"];
            $headers = [];
            foreach ($serverParams as $k => $v) {
                if (str_starts_with($k, 'HTTP_')) {
                    $headerName = str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($k, 5)))));
                    if (in_array(strtolower($k), $sensitive)) {
                        $headers[$headerName] = "[REDACTED]";
                    } else {
                        $headers[$headerName] = (string)$v;
                    }
                }
            }

            $event = [
                "incidentId" => sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x', mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0x0fff) | 0x4000, mt_rand(0, 0x3fff) | 0x8000, mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)),
                "traceId" => $serverParams['HTTP_X_TRACE_ID'] ?? sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x', mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0x0fff) | 0x4000, mt_rand(0, 0x3fff) | 0x8000, mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)),
                "timestamp" => (int)(microtime(true) * 1000),
                "request" => [
                    "method" => $serverParams['REQUEST_METHOD'] ?? 'GET',
                    "uri" => $serverParams['REQUEST_URI'] ?? '/',
                    "queryParams" => [],
                    "headers" => $headers,
                    "body" => "",
                    "timestamp" => (int)(microtime(true) * 1000)
                ],
                "error" => [
                    "exceptionClass" => get_class($exception),
                    "message" => $exception->getMessage(),
                    "stackTrace" => $exception->getTraceAsString(),
                    "statusCode" => 500
                ],
                "deployment" => [
                    "serviceName" => $this->serviceName,
                    "serviceVersion" => "1.0.0",
                    "environment" => "production",
                    "commitHash" => "php-commit"
                ]
            ];

            $this->sendAsync(json_encode($event));
        } catch (\Throwable $e) {
            // Fail-open protection
        }
    }

    private function sendAsync(string $json): void {
        $ch = curl_init($this->collectorUrl);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
        curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, 2);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
        curl_exec($ch);
        curl_close($ch);
    }
}
