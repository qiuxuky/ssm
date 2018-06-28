package com.qiuxu.common.myplugin;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;

import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * BaseService自动生成插件
 */
public class BaseServicePlugin extends PluginAdapter {
    //BaseService
    private String name="";
    //ibatisData.base
    private String targetPackageBaseService ="";
    //ibatisData.base.impl
    private String targetPackageBaseServiceImpl ="";
    //D:\LHT\git\mybatis-generator-core\src\test\java
    private String targetBaseServiceProject="";
    //D:\LHT\git\mybatis-generator-core\src\test\java
    private String targetBaseServiceImplProject="";

    //ibatisData.bus
    private String targetPackageBusinessService ="";
    //ibatisData.bus.impl
    private String targetPackageBusinessServiceImpl ="";
    //D:\LHT\git\mybatis-generator-core\src\test\java
    private String targetBusinessServiceProject="";

    private String targetBusinessServiceImplProject="";

    //Example
    private String searchString="";
    //Criteria
    private String replaceString="";
    //ibatisData.ff.BaseMapper
    private String baseDaoPackage ="";
    @Override
    public boolean validate(List<String> warnings) {
        name =properties.get("name").toString();
        targetPackageBaseService =properties.get("targetPackageBaseService").toString();
        targetPackageBaseServiceImpl =properties.get("targetPackageBaseServiceImpl").toString();
        targetBaseServiceProject =properties.get("targetBaseServiceProject").toString();
        targetBaseServiceImplProject =properties.get("targetBaseServiceImplProject").toString();

        targetPackageBusinessService =properties.get("targetPackageBusinessService").toString();
        targetPackageBusinessServiceImpl =properties.get("targetPackageBusinessServiceImpl").toString();
        targetBusinessServiceProject =properties.get("targetBusinessServiceProject").toString();
        targetBusinessServiceImplProject =properties.get("targetBusinessServiceImplProject").toString();

        searchString =properties.get("searchString").toString();
        replaceString =properties.get("replaceString").toString();
        baseDaoPackage =properties.get("baseDaoPackage").toString();


        boolean valid = stringHasValue(name)
                && stringHasValue(targetPackageBaseService)  && stringHasValue(targetPackageBaseServiceImpl)
                && stringHasValue(targetBaseServiceProject)
                && stringHasValue(targetPackageBusinessService)
                && stringHasValue(targetPackageBusinessServiceImpl)
                && stringHasValue(targetBusinessServiceProject)

                && stringHasValue(searchString)
                && stringHasValue(replaceString)
                && stringHasValue(baseDaoPackage);

        if(!valid){
            if (!stringHasValue(name)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "name")); //$NON-NLS-1$
            }
            if (!stringHasValue(targetPackageBaseService)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "targetPackageBaseService")); //$NON-NLS-1$
            }
            if (!stringHasValue(targetPackageBaseServiceImpl)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "targetPackageBaseServiceImpl")); //$NON-NLS-1$
            }

            if (!stringHasValue(targetBaseServiceProject)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "targetBaseServiceProject")); //$NON-NLS-1$
            }


            if (!stringHasValue(targetPackageBusinessService)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "targetPackageBusinessService")); //$NON-NLS-1$
            }

            if (!stringHasValue(targetPackageBusinessServiceImpl)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "targetPackageBusinessServiceImpl")); //$NON-NLS-1$
            }

            if (!stringHasValue(targetBusinessServiceProject)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "targetBusinessServiceProject")); //$NON-NLS-1$
            }


            if (!stringHasValue(searchString)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "searchString")); //$NON-NLS-1$
            }

            if (!stringHasValue(replaceString)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "replaceString")); //$NON-NLS-1$
            }

            if (!stringHasValue(baseDaoPackage)) {
                warnings.add(getString("ValidationError.99", //$NON-NLS-1$
                        "MyBaseServicePlugin", //$NON-NLS-1$
                        "baseDaoPackage")); //$NON-NLS-1$
            }
        }
        return valid;
    }



    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String recordType=introspectedTable.getBaseRecordType();
        String mapperType=introspectedTable.getMyBatis3JavaMapperType();
        List<IntrospectedColumn> introspectedColumns=introspectedTable.getPrimaryKeyColumns();
        String pk="String";
        if(introspectedColumns!=null && introspectedColumns.size()>0){
             pk=introspectedColumns.get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
        }

        String exampleType = introspectedTable.getExampleType();
        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        files.add(generatedBaseServiceFile());
        files.add(generatedBaseServiceImplFile());
        files.add(generatedBusinessServiceFile(recordType,exampleType,pk));
        files.add(generatedBusinessServiceImplFile(recordType,exampleType,mapperType,pk));
        return files;
    }

    /**
     * 生成业务接口
     * @param roecordType
     * @param exampleType
     * @param pk
     * @return
     */
    private GeneratedJavaFile generatedBusinessServiceFile(String roecordType,String exampleType,String pk) {
        //获取当前业务类型，如t_user，对应的实体是User,所以实际获取的是User
        String currentName=roecordType.substring(roecordType.lastIndexOf(".")+1,roecordType.length());
        //拼接业务接口名称；如：UserService
        String businessService =currentName+"Service";
        FullyQualifiedJavaType serviceInterfaceType = new FullyQualifiedJavaType(targetPackageBusinessService+ "." + businessService);
        Interface service = new Interface(serviceInterfaceType);
        service.setVisibility(JavaVisibility.PUBLIC);
        service.addImportedType(new FullyQualifiedJavaType(roecordType));
        //引入BaseService
        service.addImportedType(new FullyQualifiedJavaType(targetPackageBaseService+"."+name));

        //继承BaseService
        service.addSuperInterface(new FullyQualifiedJavaType(name+" <"+currentName+ ">"));
        JavaFormatter javaFormatter = new DefaultJavaFormatter();
        javaFormatter.setContext(context);
        return new GeneratedJavaFile(service,targetBusinessServiceProject,javaFormatter);
    }

    /**
     * 业务类的实现
     * @param roecordType
     * @param exampleType
     * @param mapperType
     * @param pk
     * @return
     */
    private GeneratedJavaFile generatedBusinessServiceImplFile(String roecordType, String exampleType,String mapperType, String pk) {
        String currentName=roecordType.substring(roecordType.lastIndexOf(".")+1,roecordType.length());
        String mapperName=mapperType.substring(mapperType.lastIndexOf(".")+1,mapperType.length());
        //业务实现类的名称；如UserServiceImpl
        String businessServiceImpl =currentName+"ServiceImpl";
        FullyQualifiedJavaType serviceImplementType = new FullyQualifiedJavaType(targetPackageBusinessServiceImpl + "." + businessServiceImpl);
        TopLevelClass serviceImpl = new TopLevelClass(serviceImplementType);
        serviceImpl.setVisibility(JavaVisibility.PUBLIC);

        serviceImpl.addImportedType(new FullyQualifiedJavaType(roecordType));
        serviceImpl.addImportedType(new FullyQualifiedJavaType(targetPackageBusinessService +"."+currentName+"Service"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType(targetPackageBaseServiceImpl +"."+name+"Impl"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType(baseDaoPackage));
        serviceImpl.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType(mapperType));

        //业务类的实现用spring mvc的注解进行标记
        serviceImpl.addAnnotation("@Service");

        //业务类的实现继承BaseServiceImpl
        FullyQualifiedJavaType serviceInterfaceType=new FullyQualifiedJavaType(name+"Impl<"+currentName+">");
        serviceImpl.setSuperClass(serviceInterfaceType);

        //实现业务接口
        serviceImpl.addSuperInterface(new FullyQualifiedJavaType(currentName+"Service"));

        //声明业务的DAO层接口,并且使用@Autowired注入；如UserDao
        Field field = new Field();
        field.setName(StringUtils.uncapitalize(mapperName));
        field.setType(new FullyQualifiedJavaType(mapperName));
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addAnnotation("@Autowired");
        serviceImpl.addField(field);


        //重写BaseServiceImpl中的getMapper方法，并且返回当前具体业务的业务DAO
        Method method = new Method();
        method.setName("get"+ getBaseDaoName());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addBodyLine("return this."+StringUtils.uncapitalize(mapperName)+";");
        method.setReturnType(new FullyQualifiedJavaType(getBaseDaoName()+"<"+currentName+">"));
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);


        JavaFormatter javaFormatter = new DefaultJavaFormatter();
        javaFormatter.setContext(context);
        return new GeneratedJavaFile(serviceImpl,targetBusinessServiceImplProject,javaFormatter);
    }


    /**
     * 生成baseServiceImpl的实现
     * @return
     */
    private GeneratedJavaFile generatedBaseServiceImplFile() {
        //业务类的名称
        String baseServiceImpl =name+"Impl<T extends Serializable>";
        FullyQualifiedJavaType serviceImplementType = new FullyQualifiedJavaType(targetPackageBaseServiceImpl + "." + baseServiceImpl);
        TopLevelClass serviceImpl = new TopLevelClass(serviceImplementType);
        serviceImpl.setVisibility(JavaVisibility.PUBLIC);
        serviceImpl.setAbstract(true);
        serviceImpl.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType("com.github.pagehelper.PageInfo"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType("java.io.Serializable"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        serviceImpl.addImportedType(new FullyQualifiedJavaType(targetPackageBaseService+"."+name));
        serviceImpl.addImportedType(new FullyQualifiedJavaType(baseDaoPackage));

        //实现BaseService接口
        FullyQualifiedJavaType serviceInterfaceType=new FullyQualifiedJavaType(targetPackageBaseService + "." +name+"<T>");
        serviceImpl.addSuperInterface(serviceInterfaceType);

        /**
         * 添加抽象方法，注入BaseDao
         * public abstract BaseMapper<T> getBaseMapper();
         */
        Method parentMethod=new Method();
        String basedao = getBaseDaoName();
        parentMethod.setName("get"+ getBaseDaoName());
        parentMethod.setVisibility(JavaVisibility.PUBLIC);
        parentMethod.setReturnType(new FullyQualifiedJavaType( getBaseDaoName()+"<T>"));
        serviceImpl.addMethod(parentMethod);

        //获取getBaseMapper方法名称
        String parentName=parentMethod.getName()+"()";
        

        //开始实现BaseService中的方法
        //selectById;
        Method method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Object"));
        method.setName("selectById");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Serializable"),"id"));
        method.addBodyLine("return "+parentName+".selectById"+"(id);");
        // Spring 注解
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);

        //Object selectOne(@Param("item")Object obj);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Object"));
        method.setName("selectOne");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        method.addBodyLine("return "+parentName+".selectOne"+"(obj);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public List<?> selectList(@Param("item")Object obj);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("List<?>"));
        method.setName("selectList");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        method.addBodyLine("return "+parentName+".selectList(obj);");
        // Spring 注解
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public PageInfo<T> selectPage(@Param("item")Object obj, @Param("page")PageInfo<T> page);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("PageInfo<T>"));
        method.setName("selectPage");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"page\")PageInfo<T>"),"page"));
        method.addBodyLine(0, "List<T> list = "+parentName+".selectPage(obj,page);");
        method.addBodyLine("return new PageInfo<T>(list);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);

        //public int save(@Param("item")Object obj);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("save");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        method.addBodyLine("return "+parentName+".save(obj);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public int batchSave(List<?> list);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("batchSave");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<?>"),"list"));
        method.addBodyLine("return "+parentName+".batchSave(list);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public int update(@Param("item")Object obj);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("update");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        method.addBodyLine("return "+parentName+".update(obj);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public int batchUpdate(List<?> list);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("batchUpdate");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<?>"),"list"));
        method.addBodyLine("return "+parentName+".batchUpdate(list);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public int delById(Serializable id);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("delById");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Serializable"),"id"));
        method.addBodyLine("return "+parentName+".delById(id);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public int delList(List<?> list);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("delList");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<?>"),"list"));
        method.addBodyLine("return "+parentName+".delList(list);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public int delArray(int[] ids);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("delArray");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("long[]"),"ids"));
        method.addBodyLine("return "+parentName+".delArray(ids);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);
        
        //public int count(Object obj);
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.setName("count");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Object"),"obj"));
        method.addBodyLine("return "+parentName+".count(obj);");
        method.addAnnotation("@Override");
        serviceImpl.addMethod(method);

        JavaFormatter javaFormatter = new DefaultJavaFormatter();
        javaFormatter.setContext(context);
        return new GeneratedJavaFile(serviceImpl,targetBaseServiceImplProject,javaFormatter);
    }


    /**
     * 生成BaseService接口
     * @return
     */
    private GeneratedJavaFile generatedBaseServiceFile() {
        String baseServiceClass =name+"<T extends Serializable>";
        FullyQualifiedJavaType serviceInterfaceType = new FullyQualifiedJavaType(targetPackageBaseService + "." + baseServiceClass);
        Interface service = new Interface(serviceInterfaceType);
        service.setVisibility(JavaVisibility.PUBLIC);
        service.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        service.addImportedType(new FullyQualifiedJavaType("java.io.Serializable"));
        service.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        service.addImportedType(new FullyQualifiedJavaType("com.github.pagehelper.PageInfo"));

        //selectById
        Method method=new Method();
        method.setName("selectById");
        method.setVisibility(JavaVisibility.PUBLIC);//由于不起作用,讲其放到返回值前面：如下所示
        method.setReturnType(new FullyQualifiedJavaType("public Object"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Serializable"),"id"));
        service.addMethod(method);
        
        //selectOne
        method=new Method();
        method.setName("selectOne");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public Object"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        service.addMethod(method);
        
        //selectList
        method=new Method();
        method.setName("selectList");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public List<?>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        service.addMethod(method);
        
        //selectPage
        method=new Method();
        method.setName("selectPage");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public PageInfo<T>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"page\")PageInfo<T>"),"page"));
        service.addMethod(method);
        
        //save
        method=new Method();
        method.setName("save");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        service.addMethod(method);
        
        //batchSave
        method=new Method();
        method.setName("batchSave");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<?>"),"list"));
        service.addMethod(method);
        
        //update
        method=new Method();
        method.setName("update");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("@Param(\"item\")Object"),"obj"));
        service.addMethod(method);
        
        //batchUpdate
        method=new Method();
        method.setName("batchUpdate");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<?>"),"list"));
        service.addMethod(method);
        
        //delById
        method=new Method();
        method.setName("delById");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Serializable"),"id"));
        service.addMethod(method);
        
        //delList
        method=new Method();
        method.setName("delList");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<?>"),"list"));
        service.addMethod(method);
        
        //delArray
        method=new Method();
        method.setName("delArray");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("long[]"),"ids"));
        service.addMethod(method);
        
        //count
        method=new Method();
        method.setName("count");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("public int"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Object"),"obj"));
        service.addMethod(method);


        JavaFormatter javaFormatter = new DefaultJavaFormatter();
        javaFormatter.setContext(context);
        return new GeneratedJavaFile(service,targetBaseServiceProject,javaFormatter);
    }

    /**
     * 获取baseDao的名称
     * @return
     */
    private String getBaseDaoName(){
        return baseDaoPackage.substring(baseDaoPackage.lastIndexOf(".")+1,baseDaoPackage.length());
    }

}
