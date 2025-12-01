// Slice definitions for Authentication
// Defines interfaces for operator authentication via DataCenter

module SITM {
    
    // Sequence of zone IDs
    sequence<string> StringSeq;
    
    // Operator credentials for login
    struct OperatorCredentials {
        string username;
        string password;
    };
    
    // Authentication result with operator data
    struct OperatorAuthResult {
        long operatorId;
        string username;
        StringSeq assignedZones;
        string token;
    };
    
    // Authentication service interface
    interface Authenticator {
        /**
         * Authenticate an operator.
         * @param credentials Operator credentials (username, password)
         * @return OperatorAuthResult with token and assigned zones, or null if authentication fails
         */
        OperatorAuthResult authenticateOperator(OperatorCredentials credentials);
        
        /**
         * Validate a session token.
         * @param token Session token
         * @return true if token is valid, false otherwise
         */
        bool validateToken(string token);
        
        /**
         * Invalidate a session token (logout).
         * @param token Session token to invalidate
         */
        void invalidateToken(string token);
    };
};
