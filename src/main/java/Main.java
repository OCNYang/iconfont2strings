import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n--------------- iconfont2strings 开始运行 ---------------");
        System.out.println("-- 运行要求：请将 iconfont2strings.jar 放到 iconfont.cn 下载的压缩包的解压文件夹内，");
        System.out.println("-- 即保证 iconfont2strings.jar 和 iconfont.json 文件在同一文件夹下。");
        System.out.println("----------------------------------------------------------\n");
        String json = readJsonFile("iconfont.json");

        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray glyphs = jsonObject.getAsJsonArray("glyphs");
            if (glyphs != null && glyphs.size() > 0) {
                String name = jsonObject.get("name").getAsString();
                File file = new File("strings_iconfont_" + name + ".xml");
                if (!file.exists()) {
                    file.createNewFile();
                    System.out.println("创建字符串资源文件成功：" + file.getName());
                }

                Scanner scanner = new Scanner(System.in);
                System.out.println("▶ 是否需要自定义字符串资源的名字前缀(默认前缀：iconfont_)：0：需要，1：不需要");
                int i = scanner.nextInt();
                String inputPrefix = null;
                if (i == 0) {
                    System.out.println("\n▶ 请输入字符串资源的名字前缀(例：iconfont_)：");
                    inputPrefix = scanner.next();
                }

                String prefixName = "";

                if (inputPrefix == null || inputPrefix.equals("") || inputPrefix.isEmpty()) {
                    prefixName = "iconfont_";
                } else {
                    prefixName = inputPrefix;
                }
                System.out.println("\n▶ 请选择要生成的字符格式形式：");
                System.out.println("0：TAXI 形式[推荐]  例：<string name=\"iconfont_tuya\">\\uf520</string>");
                System.out.println("1：XML  形式[原有]  例：<string name=\"iconfont_tuya\">&#xf520;</string>");
                int type = scanner.nextInt();

                FileWriter fileWriter = new FileWriter(file.getName(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                System.out.println("\n▶ 开始写入字符串...");
                bufferedWriter.write("<resources>\n");
                ProgressBar progressBar = ProgressBar.build();
                int size = glyphs.size();
                JsonElement glyph;
                if (type != 1) {
                    for (int j = 0; j < size; j++) {
                        progressBar.process(100 * j / size);
                        glyph = glyphs.get(j);
                        JsonObject iconfontJsonObject = glyph.getAsJsonObject();
                        bufferedWriter.write("    <string name=\"" +
                                prefixName +
                                iconfontJsonObject.get("font_class").getAsString().replaceAll("-", "_") +
                                "\">\\u" +
                                iconfontJsonObject.get("unicode").getAsString() +
                                "</string>\n");
                    }
                } else {
                    for (int j = 0; j < size; j++) {
                        progressBar.process(100 * j / size);
                        glyph = glyphs.get(j);
                        JsonObject iconfontJsonObject = glyph.getAsJsonObject();
                        bufferedWriter.write("    <string name=\"" +
                                prefixName +
                                iconfontJsonObject.get("font_class").getAsString().replaceAll("-", "_") +
                                "\">&#x" +
                                iconfontJsonObject.get("unicode").getAsString() +
                                ";</string>\n");
                    }
                }
                bufferedWriter.write("</resources>");
                bufferedWriter.close();
                System.out.println("写入完成！");

                System.out.println("\n--------------- 程序运行结束 ---------------");
                System.out.println("--------------- 作者:OCNYang ---------------\n");
            } else {
                System.out.println("iconfont.json 内没有数据。");
            }
        } catch (FileNotFoundException e) {
            System.out.println("没有找到 iconfont.json 文件。");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
