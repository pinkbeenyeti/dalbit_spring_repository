package dalbit.adapter.rest.internal.auth.dto.request;

public record QueueResourceAuthRequest(
    String username,
    String vhost,
    String resource,
    String name,
    String permission
) {

}
