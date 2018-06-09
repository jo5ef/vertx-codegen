package io.vertx.codegen;

import io.vertx.codegen.doc.Doc;
import io.vertx.codegen.doc.Text;
import io.vertx.codegen.overloadcheck.MethodOverloadChecker;
import io.vertx.codegen.type.ClassTypeInfo;
import io.vertx.codegen.type.ParameterizedTypeInfo;
import io.vertx.codegen.type.TypeInfo;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="https://github.com/slinkydeveloper">Francesco Guardiani</a>
 */
public class OpenApiProxyModel extends ProxyModel {

  public OpenApiProxyModel(ProcessingEnvironment env, MethodOverloadChecker methodOverloadChecker, Messager messager, Map<String, TypeElement> sources, Elements elementUtils, Types typeUtils, TypeElement modelElt) {
    super(env, methodOverloadChecker, messager, sources, elementUtils, typeUtils, modelElt);
  }

  @Override
  protected MethodInfo createMethodInfo(Set<ClassTypeInfo> ownerTypes, String methodName, String comment, Doc doc, MethodKind kind, TypeInfo returnType, Text returnDescription, boolean isFluent, boolean isCacheReturn, List<ParamInfo> mParams, ExecutableElement methodElt, boolean isStatic, boolean isDefault, ArrayList<TypeParamInfo.Method> typeParams, TypeElement declaringElt, boolean methodDeprecated) {
    ProxyMethodInfo baseInfo = (ProxyMethodInfo) super.createMethodInfo(ownerTypes, methodName, comment, doc, kind, returnType, returnDescription, isFluent, isCacheReturn, mParams, methodElt, isStatic, isDefault, typeParams, declaringElt, methodDeprecated);
    if (!isStatic && !baseInfo.isProxyClose() && !baseInfo.isProxyIgnore()) {
      ParamInfo shouldBeRequestContextParam = mParams.get(mParams.size() - 2);
      if (shouldBeRequestContextParam == null || !shouldBeRequestContextParam.type.getName().equals("io.vertx.ext.web.api.RequestContext")) {
        throw new GenException(this.modelElt, "Method " + methodName + "should have the second to last parameter with type io.vertx.ext.web.api.RequestContext");
      }
      ParamInfo shouldBeHandler = mParams.get(mParams.size() - 1);
      if (kind != MethodKind.HANDLER || shouldBeHandler == null) {
        TypeInfo shouldBeOperationResult = ((ParameterizedTypeInfo) ((ParameterizedTypeInfo) shouldBeHandler.getType()).getArg(0)).getArg(0);
        if (!"io.vertx.ext.web.api.OperationResult".equals(shouldBeOperationResult.getName()))
          throw new GenException(this.modelElt, "Method " + methodName + "should last parameter should be an handler of type Handler<AsyncResult<io.vertx.ext.web.api.OperationResult>>");
      }
      return new OpenApiProxyMethodInfo(baseInfo);
    } else {
      return baseInfo;
    }
  }
}
