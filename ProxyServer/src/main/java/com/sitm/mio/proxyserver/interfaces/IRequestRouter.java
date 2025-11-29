package com.sitm.mio.proxyserver.interfaces;

/**
 * Interface for HTTP request routing.
 * Routes incoming requests to appropriate handlers or services.
 * 
 * Realized by: RequestRouter
 */
public interface IRequestRouter {
    
    /**
     * Route an incoming request to the appropriate handler.
     * 
     * @param method HTTP method (GET, POST, etc.)
     * @param path Request path
     * @param headers Request headers
     * @param body Request body
     * @return Response object
     */
    Object route(String method, String path, Object headers, Object body);
    
    /**
     * Register a route handler.
     * 
     * @param method HTTP method
     * @param path Path pattern
     * @param handler Handler function
     */
    void registerRoute(String method, String path, Object handler);
    
    /**
     * Check if route exists.
     * 
     * @param method HTTP method
     * @param path Request path
     * @return true if route is registered
     */
    boolean hasRoute(String method, String path);
    
    /**
     * Remove a registered route.
     * 
     * @param method HTTP method
     * @param path Path pattern
     */
    void unregisterRoute(String method, String path);
}
