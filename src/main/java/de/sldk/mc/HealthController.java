package de.sldk.mc;

import de.sldk.mc.health.HealthChecks;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class HealthController extends Handler.Abstract {

    private final HealthChecks checks;

    private HealthController(final HealthChecks checks) {
        this.checks = checks;
    }

    public static Handler create(final HealthChecks checks) {
        return new HealthController(checks);
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        response.setStatus(checks.isHealthy() ? HttpStatus.OK_200 : HttpStatus.SERVICE_UNAVAILABLE_503);
        callback.succeeded();
        return true;
    }
}
