package dalbit.adapter.rest.internal.auth.dto.request;

public record QueueVhostAuthRequest(
    String username,
    String vhost,
    String ip) {

}
