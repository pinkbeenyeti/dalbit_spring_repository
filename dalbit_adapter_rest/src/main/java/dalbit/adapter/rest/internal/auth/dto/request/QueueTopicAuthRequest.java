package dalbit.adapter.rest.internal.auth.dto.request;

public record QueueTopicAuthRequest(
    String username,
    String vhost,
    String resource,
    String name,
    String permission,
    String routing_key
) {

}
