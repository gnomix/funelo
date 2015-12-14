package de.zalando.funelo;

public class FuneloException extends RuntimeException {

    private static final long serialVersionUID = -4513767991110514833L;

    public FuneloException() {
        super();
    }

    public FuneloException(final String message) {
        super(message);
    }

    public FuneloException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FuneloException(final Throwable cause) {
        super(cause);
    }
}
