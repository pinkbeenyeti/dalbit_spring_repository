package dalbit.adapter.rest.internal.auth.dto.request;

public record QueueUserAuthRequest(
    String username,
    String password
) {

}
