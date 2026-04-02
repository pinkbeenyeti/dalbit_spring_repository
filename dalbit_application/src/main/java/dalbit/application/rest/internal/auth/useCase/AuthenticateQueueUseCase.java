package dalbit.application.rest.internal.auth.useCase;

public interface AuthenticateQueueUseCase {
    boolean authenticateConnection(String userName, String password);
    boolean isResourceAuthorized(String userName, String resourceType, String name, String permission);
    boolean isTopicAuthorized(String userName, String routingKey, String permission);
}
