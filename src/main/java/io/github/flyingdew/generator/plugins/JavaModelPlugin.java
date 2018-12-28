package io.github.flyingdew.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class JavaModelPlugin extends BasePlugin {
    private boolean enableRemarks;
    private boolean enableSwaggerAnnotations;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        enableRemarks = isTrue(properties.getProperty("enableRemarks"));
        enableSwaggerAnnotations = isTrue(properties.getProperty("enableSwaggerAnnotations"));
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String remarks = introspectedTable.getRemarks();
        remarks = StringUtility.stringHasValue(remarks) ? remarks : topLevelClass.getType().getShortName();

        if (enableRemarks) {
            List<String> javaDocLines = topLevelClass.getJavaDocLines();
            addRemarksDoc(javaDocLines, remarks);
        }
        if (enableSwaggerAnnotations) {
            topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
            topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
            topLevelClass.addAnnotation("@ApiModel(\"" + remarks + "\")");
        }
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String remarks = introspectedColumn.getRemarks();
        remarks = StringUtility.stringHasValue(remarks) ? remarks : field.getName();

        if (enableRemarks) {
            List<String> javaDocLines = field.getJavaDocLines();
            addRemarksDoc(javaDocLines, remarks);
        }
        if (enableSwaggerAnnotations) {
            field.addAnnotation("@ApiModelProperty(\"" + remarks + "\")");
        }
        return true;
    }

    private void addRemarksDoc(List<String> javaDocLines, String remarks) {
        String docLine = " * " + remarks;
        if (javaDocLines.size() > 0) {
            javaDocLines.add(javaDocLines.size() - 1, docLine);
        } else {
            javaDocLines.add("/**");
            javaDocLines.add(docLine);
            javaDocLines.add(" */");
        }
    }
}
