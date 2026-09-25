export class ApiRequestError extends Error {
  constructor(message, status, fieldErrors = {}) {
    super(message);

    this.name = "ApiRequestError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}
