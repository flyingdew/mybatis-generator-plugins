package io.github.flyingdew.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class MapperAnnotationPlugin extends BasePlugin {
    @Override
    public boolean clientGenerated(Interface interfaz, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        interfaz.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaz.addAnnotation("@Mapper");
        return true;
    }
}
