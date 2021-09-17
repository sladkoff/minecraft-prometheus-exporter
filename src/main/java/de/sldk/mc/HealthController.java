package de.sldk.mc;

import de.sldk.mc.health.HealthChecks;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HealthController extends AbstractHandler {

    private final HealthChecks checks;

    private HealthController(final HealthChecks checks) {
        this.checks = checks;
    }

    public static Handler create(final HealthChecks checks) {
        return new HealthController(checks);
    }

    @Override
    public void handle(final String target,
                       final Request baseRequest,
                       final HttpServletRequest request,
                       final HttpServletResponse response) {
        if (!target.equals("/health")) return;

        response.setStatus(checks.isHealthy() ? HttpStatus.OK_200 : HttpStatus.SERVICE_UNAVAILABLE_503);
        baseRequest.setHandled(true);
    }
}
