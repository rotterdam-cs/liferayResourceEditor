package com.aimprosoft.i18n.common.message;

public class CustomMessage {
         private String message;

        private boolean error = false;

        private Throwable cause;

        public CustomMessage() {
        }

        public CustomMessage(String message) {
            this.message = message;
        }

        public CustomMessage(String message, boolean error) {
            this.message = message;
            this.error = error;
        }

        public CustomMessage(String message, boolean error, Throwable cause) {
            this.message = message;
            this.error = error;
            this.cause = cause;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public Throwable getCause() {
            return cause;
        }

        public void setCause(Throwable cause) {
            this.cause = cause;
        }
}
