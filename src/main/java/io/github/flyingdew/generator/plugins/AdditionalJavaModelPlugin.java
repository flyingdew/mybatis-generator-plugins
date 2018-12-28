package io.github.flyingdew.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class AdditionalJavaModelPlugin extends BasePlugin {
    private static final String DEFAULT_NAME_SUFFIX = "DTO";
    private String targetPackage;
    private String targetProject;
    private String nameSuffix;

    private boolean enableRemarks;
    private boolean enableSwaggerAnnotations;

    private List<String> errors = new ArrayList<>();

    @Override
    public boolean validate(List<String> warnings) {
        if (this.errors.size() > 0) {
            warnings.addAll(this.errors);
            return false;
        }
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        targetProject = properties.getProperty("targetProject");
        if (!StringUtility.stringHasValue(targetProject)) {
            errors.add("targetProject property should be set.");
        }
        targetPackage = properties.getProperty("targetPackage");
        if (!StringUtility.stringHasValue(targetPackage)) {
            errors.add("targetPackage property should be set.");
        }
        nameSuffix = properties.getProperty("nameSuffix");
        if (!StringUtility.stringHasValue(nameSuffix)) {
            nameSuffix = DEFAULT_NAME_SUFFIX;
        }

        enableRemarks = isTrue(properties.getProperty("enableRemarks"));
        enableSwaggerAnnotations = isTrue(properties.getProperty("enableSwaggerAnnotations"));
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<>();
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        String typeName = targetPackage + "." + domainObjectName + nameSuffix;

        TopLevelClass topLevelClass = new TopLevelClass(typeName);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        modelClassGenerated(topLevelClass, introspectedTable);

        Context context = introspectedTable.getContext();

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : allColumns) {
            Field field = getJavaBeansField(column);

            topLevelClass.addField(field);
            fieldGenerated(field, column);

            Method getter = JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable);
            topLevelClass.addMethod(getter);

            Method setter = JavaBeansUtil.getJavaBeansSetter(column, context, introspectedTable);
            topLevelClass.addMethod(setter);
        }

        JavaFormatter javaFormatter = new DefaultJavaFormatter();
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, targetProject,
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), javaFormatter);
        files.add(file);
        return files;
    }

    private Field getJavaBeansField(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fqjt);
        field.setName(property);

        return field;
    }

    private void modelClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
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
    }

    private void fieldGenerated(Field field, IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        remarks = StringUtility.stringHasValue(remarks) ? remarks : field.getName();

        if (enableRemarks) {
            List<String> javaDocLines = field.getJavaDocLines();
            addRemarksDoc(javaDocLines, remarks);
        }
        if (enableSwaggerAnnotations) {
            field.addAnnotation("@ApiModelProperty(\"" + remarks + "\")");
        }
    }

    private void addRemarksDoc(List<String> javaDocLines, String remarks) {
        String docLine = " * " + remarks;
        javaDocLines.add("/**");
        javaDocLines.add(docLine);
        javaDocLines.add(" */");
    }
}
