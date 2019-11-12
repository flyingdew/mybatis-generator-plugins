package io.github.flyingdew.generator.plugins;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class JavaModelPlugin extends BasePlugin {
    private boolean enableRemarks;
    private boolean enableSwagger;
    private boolean enableLombok;
    private boolean enableJpa;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        enableRemarks = isTrue(properties.getProperty("enableRemarks"));
        enableSwagger = isTrue(properties.getProperty("enableSwagger"));
        enableLombok = isTrue(properties.getProperty("enableLombok"));
        enableJpa = isTrue(properties.getProperty("enableJpa"));
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String remarks = introspectedTable.getRemarks();
        remarks = StringUtility.stringHasValue(remarks) ? remarks : topLevelClass.getType().getShortName();

        if (enableRemarks) {
            List<String> javaDocLines = topLevelClass.getJavaDocLines();
            addRemarksDoc(javaDocLines, remarks);
        }
        if (enableSwagger) {
            topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
            topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
            topLevelClass.addAnnotation("@ApiModel(\"" + remarks + "\")");
        }
        if (enableLombok) {
            topLevelClass.addImportedType("lombok.Data");
            topLevelClass.addImportedType("lombok.NoArgsConstructor");
            topLevelClass.addImportedType("lombok.AllArgsConstructor");

            topLevelClass.addAnnotation("@Data");
            topLevelClass.addAnnotation("@NoArgsConstructor");
            topLevelClass.addAnnotation("@AllArgsConstructor");
        }
        if (enableJpa) {
            topLevelClass.addImportedType("javax.persistence.*");

            FullyQualifiedTable t = introspectedTable.getFullyQualifiedTable();
            topLevelClass.addAnnotation(String.format("@Table(name =\"%s\", catalog =\"%s\", schema =\"%s\")",
                    t.getFullyQualifiedTableNameAtRuntime(),
                    t.getIntrospectedCatalog() == null ? "" : t.getIntrospectedCatalog(),
                    t.getIntrospectedSchema() == null ? "" : t.getIntrospectedSchema()));
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
        if (enableSwagger) {
            field.addAnnotation("@ApiModelProperty(\"" + remarks + "\")");
        }
        if (enableJpa) {
            if (introspectedTable.getPrimaryKeyColumns().contains(introspectedColumn)) {
                field.addAnnotation("@Id");
            }
            field.addAnnotation("@Column(name = \"" + introspectedColumn.getActualColumnName() + "\")");
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

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (enableLombok) {
            return false;
        }
        return super.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (enableLombok) {
            return false;
        }
        return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}
