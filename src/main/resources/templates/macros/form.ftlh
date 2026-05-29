<#macro flashMessages>
    <#if success??>
        <div class="flash-success">${success}</div>
    </#if>
    <#if error??>
        <div class="flash-error">${error}</div>
    </#if>
</#macro>