package generator;



import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MySQL逆向工程工具类
 * 根据表名自动生成Entity、Mapper、Service等代码
 */
public class CodeGenerator {

    /**
     * 数据库配置
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/blog?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "051213";

    /**
     * 项目根路径
     */
    private static final String PROJECT_PATH = System.getProperty("user.dir") + "/demoBlog2-service";

    /**
     * 包名配置
     */
    private static final String PACKAGE_NAME = "com.example";

    /**
     * 作者信息
     */
    private static final String AUTHOR = "CodeGenerator";

    /**
     * 需要生成的表名列表
     */
    private static final String[] TABLE_NAMES = {
            "social_tax_msg","tax_receive_msg"
    };

    public static void main(String[] args) {
        if (TABLE_NAMES.length == 0) {
            System.out.println("请在TABLE_NAMES数组中添加需要生成代码的表名！");
            return;
        }

        System.out.println("========== 开始生成代码 ==========");
        System.out.println("项目路径: " + PROJECT_PATH);
        System.out.println("包名: " + PACKAGE_NAME);
        System.out.println("生成表: " + String.join(", ", TABLE_NAMES));
        System.out.println("====================================");

        try {
            generateCode(TABLE_NAMES);

            System.out.println("\n========== 开始生成 DTO ==========");
            generateDTOs(TABLE_NAMES);
            System.out.println("========== DTO 生成完成 ==========");

            System.out.println("\n========== 代码生成完成 ==========");
            System.out.println("生成位置:");
            System.out.println("  Entity: " + PROJECT_PATH + "/src/main/java/" + PACKAGE_NAME.replace(".", "/") + "/entity/");
            System.out.println("  Mapper: " + PROJECT_PATH + "/src/main/java/" + PACKAGE_NAME.replace(".", "/") + "/mapper/");
            System.out.println("  Service: " + PROJECT_PATH + "/src/main/java/" + PACKAGE_NAME.replace(".", "/") + "/service/");
            System.out.println("  XML: " + PROJECT_PATH + "/src/main/resources/mapper/");

            System.out.println("  DTO: D:/Blog/code/springboot_work/demoBlog2/demoBlog2-api/src/main/java/com/example/api/dto/");

            System.out.println("====================================");
        } catch (Exception e) {
            System.err.println("\n========== 代码生成失败 ==========");
            System.err.println("错误类型: " + e.getClass().getName());
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            System.err.println("====================================");
        }
    }

    /**
     * 执行代码生成 - 使用默认模板
     */
    private static void generateCode(String[] tableNames) {

        // 【临时方案】如果不升级版本，先手动删掉旧文件
        for (String tableName : tableNames) {
            String entityName = convertTableNameToEntityName(tableName);
            String basePath = PROJECT_PATH + "/src/main/java/" + PACKAGE_NAME.replace(".", "/");
            new File(basePath + "/entity/" + entityName + ".java").delete();
            new File(basePath + "/mapper/" + entityName + "Mapper.java").delete();
            // ... 删掉其他你想覆盖的文件
        }

        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                .globalConfig(builder -> {
                    builder.author(AUTHOR)
                            .outputDir(PROJECT_PATH + "/src/main/java")
                            .commentDate("yyyy-MM-dd")
                            .disableOpenDir();
                })
                .packageConfig(builder -> {
                    builder.parent(PACKAGE_NAME)
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, PROJECT_PATH + "/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableNames)
                            .addTablePrefix("t_", "sys_")
                            // 开启全局文件覆盖
                            //.enableFileOverride()
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .logicDeleteColumnName("deleted")
                            .mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList()
                            .serviceBuilder()
                            .formatServiceFileName("I%sService")
                            .formatServiceImplFileName("%sServiceImpl");
                })
                .execute();
    }

    /**
     * 便捷方法：生成单个表的代码
     */
    public static void generateSingleTable(String tableName) {
        generateCode(new String[]{tableName});
    }

    private static void generateDTOs(String[] tableNames) throws IOException {
        String entityPath = PROJECT_PATH + "/src/main/java/" + PACKAGE_NAME.replace(".", "/") + "/entity/";
        String dtoOutputPath = "D:/Blog/code/springboot_work/demoBlog2/demoBlog2-api/src/main/java/com/example/api/dto";

        File dtoDir = new File(dtoOutputPath);
        if (!dtoDir.exists()) {
            dtoDir.mkdirs();
        }

        for (String tableName : tableNames) {
            String entityName = convertTableNameToEntityName(tableName);
            String entityFilePath = entityPath + entityName + ".java";

            File entityFile = new File(entityFilePath);
            if (!entityFile.exists()) {
                System.out.println("警告: Entity 文件不存在 - " + entityFilePath);
                continue;
            }

            String entityContent = new String(Files.readAllBytes(Paths.get(entityFilePath)));
            String dtoContent = generateDTOContent(entityName, entityContent);

            String dtoFilePath = dtoOutputPath + "/" + entityName + "DTO.java";
            try (FileWriter writer = new FileWriter(dtoFilePath)) {
                writer.write(dtoContent);
            }

            System.out.println("已生成 DTO: " + dtoFilePath);
        }
    }

    /**
     * 将表名转换为实体类名
     */
    private static String convertTableNameToEntityName(String tableName) {
        String nameWithoutPrefix = tableName;
        if (tableName.startsWith("sys_")) {
            nameWithoutPrefix = tableName.substring(4);
        } else if (tableName.startsWith("t_")) {
            nameWithoutPrefix = tableName.substring(2);
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;
        for (char c : nameWithoutPrefix.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    /**
     * 根据 Entity 内容生成 DTO 内容
     */
    private static String generateDTOContent(String entityName, String entityContent) {
        StringBuilder dto = new StringBuilder();
        dto.append("package com.example.api.dto;\n\n");
        dto.append("import lombok.Data;\n");

        boolean hasLocalDateTime = entityContent.contains("LocalDateTime");
        boolean hasLocalDate = entityContent.contains("LocalDate");

        if (hasLocalDateTime || hasLocalDate) {
            dto.append("import java.time.LocalDateTime;\n");
            if (hasLocalDate) {
                dto.append("import java.time.LocalDate;\n");
            }
        }

        dto.append("\n@Data\n");
        dto.append("public class ").append(entityName).append("DTO {\n");

        Pattern pattern = Pattern.compile("private\\s+(\\S+)\\s+(\\w+);");
        Matcher matcher = pattern.matcher(entityContent);

        while (matcher.find()) {
            String fieldType = matcher.group(1);
            String fieldName = matcher.group(2);

            if (!"serialVersionUID".equals(fieldName)) {
                dto.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n");
            }
        }

        dto.append("}\n");
        return dto.toString();
    }


}
