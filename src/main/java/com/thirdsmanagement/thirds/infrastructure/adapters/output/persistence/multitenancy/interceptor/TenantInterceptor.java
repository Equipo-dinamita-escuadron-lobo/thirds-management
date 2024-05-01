package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.interceptor;


import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.util.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Component
public class TenantInterceptor implements WebRequestInterceptor {
    @Override
    public void preHandle(WebRequest request) throws Exception {
//        UsernamePasswordAuthenticationToken tokenAuth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        //Validate the tokenAuth
//        if(tokenAuth != null){
//            String tenantId = tokenAuth.getCredentials().toString();
//            TenantContext.setTenantId(tenantId);
//        }

        TenantContext.setTenantId("1");


    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }
}
