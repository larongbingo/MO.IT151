using Microsoft.AspNetCore.Authorization;

namespace MOIT151.Web.Authorization;

public class MOIT151AuthorizationHandler(IExternalIdentityService identityService) 
    : AuthorizationHandler<MOIT151AuthorizationRequirement>
{
    protected override async Task HandleRequirementAsync(AuthorizationHandlerContext context, MOIT151AuthorizationRequirement requirement)
    {
        var user = await identityService.GetUserAsync();
        if (user is null)
        {
            context.Fail(new AuthorizationFailureReason(this, "User must have an account and logged in"));
            return;
        }
        
        context.Succeed(requirement);
    }
}

public class MOIT151AuthorizationRequirement : IAuthorizationRequirement;