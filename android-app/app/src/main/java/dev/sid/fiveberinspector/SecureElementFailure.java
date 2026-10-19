package dev.sid.fiveberinspector;

/** Fixed diagnostic codes; never forward vendor exception messages or card identifiers. */
final class SecureElementFailure extends SecurityException {
    enum Reason {
        NO_SERVICE, SYSTEM_INTERFACE_UNAVAILABLE, ACCESS_DENIED, NO_READABLE_EUICC,
        AMBIGUOUS_EUICC, CHANNEL_CLEANUP_FAILED
    }
    final Reason reason;
    SecureElementFailure(Reason reason) { super(reason.name()); this.reason = reason; }
    static SecureElementFailure find(Throwable error) {
        for (int depth = 0; error != null && depth < 12; depth++, error = error.getCause())
            if (error instanceof SecureElementFailure) return (SecureElementFailure) error;
        return null;
    }
    static String message(String code) {
        if (Reason.ACCESS_DENIED.name().equals(code))
            return "The system denies access to the eUICC applet. ADB or Shizuku alone cannot grant card access on this ROM.";
        if (Reason.NO_SERVICE.name().equals(code))
            return "No supported Secure Element service is available on this device.";
        if (Reason.SYSTEM_INTERFACE_UNAVAILABLE.name().equals(code))
            return "This ROM does not expose the system Secure Element interfaces required by this build.";
        if (Reason.NO_READABLE_EUICC.name().equals(code))
            return "No readable eUICC was found. Check the card and this ROM's Secure Element access policy.";
        if (Reason.AMBIGUOUS_EUICC.name().equals(code))
            return "More than one readable eUICC was found. Automatic card selection is blocked.";
        if (Reason.CHANNEL_CLEANUP_FAILED.name().equals(code))
            return "A card channel could not be closed. Stop and verify the card connection before continuing.";
        return "";
    }
}
