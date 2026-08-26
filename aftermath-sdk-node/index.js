const http = require('http');
const crypto = require('crypto');

class AftermathNodeSdk {
    constructor(options = {}) {
        this.collectorUrl = options.collectorUrl || 'http://localhost:8090/api/v1/incidents';
        this.serviceName = options.serviceName || 'node-express-service';
        this.enabled = options.enabled !== false;
    }

    redactHeaders(headers) {
        const sensitive = ['authorization', 'cookie', 'x-api-key', 'proxy-authorization'];
        const redacted = {};
        for (const [key, val] of Object.entries(headers || {})) {
            if (sensitive.includes(key.toLowerCase())) {
                redacted[key] = '[REDACTED]';
            } else {
                redacted[key] = val;
            }
        }
        return redacted;
    }

    redactBody(body) {
        if (!body) return body;
        let str = typeof body === 'string' ? body : JSON.stringify(body);
        str = str.replace(/eyJ[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.[A-Za-z0-9-_.+/=]*/g, '[TOKEN_REDACTED]');
        str = str.replace(/"(password|secret|token|apiKey)":\s*"[^"]*"/gi, '"$1": "[REDACTED]"');
        return str;
    }

    expressMiddleware() {
        return (err, req, res, next) => {
            if (this.enabled && err) {
                try {
                    const incidentEvent = {
                        incidentId: crypto.randomUUID(),
                        traceId: req.headers['x-trace-id'] || req.headers['traceparent'] || crypto.randomUUID(),
                        timestamp: Date.now(),
                        request: {
                            method: req.method,
                            uri: req.originalUrl || req.url,
                            queryParams: req.query || {},
                            headers: this.redactHeaders(req.headers),
                            body: this.redactBody(req.body),
                            timestamp: Date.now()
                        },
                        error: {
                            exceptionClass: err.name || 'Error',
                            message: err.message || 'Express Error',
                            stackTrace: err.stack || '',
                            statusCode: res.statusCode >= 400 ? res.statusCode : 500
                        },
                        deployment: {
                            serviceName: this.serviceName,
                            serviceVersion: '1.0.0',
                            environment: process.env.NODE_ENV || 'development',
                            commitHash: 'node-commit'
                        }
                    };

                    this.sendAsync(incidentEvent);
                } catch (e) {
                    console.warn('Aftermath Node SDK fail-open warning:', e.message);
                }
            }
            next(err);
        };
    }

    sendAsync(event) {
        try {
            const data = JSON.stringify(event);
            const url = new URL(this.collectorUrl);
            const req = http.request(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
                },
                timeout: 2000
            });
            req.on('error', () => {}); // Fail-open: ignore transport errors
            req.write(data);
            req.end();
        } catch (ignored) {}
    }
}

module.exports = AftermathNodeSdk;
