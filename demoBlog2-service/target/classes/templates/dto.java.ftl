package ${package.Dto};

import lombok.Data;
<#list table.importPackages as pkg>
    <#if pkg?index_of("java.time") != -1>
        import ${pkg};
    </#if>
</#list>

@Data
public class ${entity}DTO {
<#list table.fields as field>
    private ${field.propertyType} ${field.propertyName};
</#list>
}