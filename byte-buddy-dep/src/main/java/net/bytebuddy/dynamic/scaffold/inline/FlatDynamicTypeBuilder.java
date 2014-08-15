package net.bytebuddy.dynamic.scaffold.inline;

import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.asm.ClassVisitorWrapper;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.*;
import net.bytebuddy.dynamic.scaffold.subclass.SubclassInstrumentationTarget;
import net.bytebuddy.instrumentation.attribute.FieldAttributeAppender;
import net.bytebuddy.instrumentation.attribute.MethodAttributeAppender;
import net.bytebuddy.instrumentation.attribute.TypeAttributeAppender;
import net.bytebuddy.instrumentation.method.MethodLookupEngine;
import net.bytebuddy.instrumentation.method.matcher.MethodMatcher;
import net.bytebuddy.instrumentation.type.TypeDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A dynamic type builder which enhances a given type without creating a subclass.
 *
 * @param <T> The most specific type that is known to be represented by the enhanced type.
 */
public class FlatDynamicTypeBuilder<T> extends DynamicType.Builder.AbstractBase<T> {

    /**
     * A locator for finding a class file.
     */
    private final ClassFileLocator classFileLocator;

    /**
     * Creates a new immutable type builder for enhancing a given class.
     *
     * @param classFileVersion                      The class file version for the created dynamic type.
     * @param namingStrategy                        The naming strategy for naming the dynamic type.
     * @param levelType                             A description of the enhanced type.
     * @param interfaceTypes                        A list of interfaces that should be implemented by the created dynamic type.
     * @param modifiers                             The modifiers to be represented by the dynamic type.
     * @param attributeAppender                     The attribute appender to apply onto the dynamic type that is created.
     * @param ignoredMethods                        A matcher for determining methods that are to be ignored for instrumentation.
     * @param bridgeMethodResolverFactory           A factory for creating a bridge method resolver.
     * @param classVisitorWrapperChain              A chain of ASM class visitors to apply to the writing process.
     * @param fieldRegistry                         The field registry to apply to the dynamic type creation.
     * @param methodRegistry                        The method registry to apply to the dynamic type creation.
     * @param methodLookupEngineFactory             The method lookup engine factory to apply to the dynamic type creation.
     * @param defaultFieldAttributeAppenderFactory  The field attribute appender factory that should be applied by default if
     *                                              no specific appender was specified for a given field.
     * @param defaultMethodAttributeAppenderFactory The method attribute appender factory that should be applied by default
     *                                              if no specific appender was specified for a given method.
     * @param classFileLocator                      A locator for finding a class file.
     */
    public FlatDynamicTypeBuilder(ClassFileVersion classFileVersion,
                                  NamingStrategy namingStrategy,
                                  TypeDescription levelType,
                                  List<? extends TypeDescription> interfaceTypes,
                                  int modifiers,
                                  TypeAttributeAppender attributeAppender,
                                  MethodMatcher ignoredMethods,
                                  BridgeMethodResolver.Factory bridgeMethodResolverFactory,
                                  ClassVisitorWrapper.Chain classVisitorWrapperChain,
                                  FieldRegistry fieldRegistry,
                                  MethodRegistry methodRegistry,
                                  MethodLookupEngine.Factory methodLookupEngineFactory,
                                  FieldAttributeAppender.Factory defaultFieldAttributeAppenderFactory,
                                  MethodAttributeAppender.Factory defaultMethodAttributeAppenderFactory,
                                  ClassFileLocator classFileLocator) {
        this(classFileVersion,
                namingStrategy,
                levelType,
                new ArrayList<TypeDescription>(interfaceTypes),
                modifiers,
                attributeAppender,
                ignoredMethods,
                bridgeMethodResolverFactory,
                classVisitorWrapperChain,
                fieldRegistry, methodRegistry,
                methodLookupEngineFactory,
                defaultFieldAttributeAppenderFactory,
                defaultMethodAttributeAppenderFactory,
                Collections.<FieldToken>emptyList(),
                Collections.<MethodToken>emptyList(),
                classFileLocator);
    }

    /**
     * Creates a new immutable type builder for enhancing a given class.
     *
     * @param classFileVersion                      The class file version for the created dynamic type.
     * @param namingStrategy                        The naming strategy for naming the dynamic type.
     * @param levelType                             A description of the enhanced type.
     * @param interfaceTypes                        A list of interfaces that should be implemented by the created dynamic type.
     * @param modifiers                             The modifiers to be represented by the dynamic type.
     * @param attributeAppender                     The attribute appender to apply onto the dynamic type that is created.
     * @param ignoredMethods                        A matcher for determining methods that are to be ignored for instrumentation.
     * @param bridgeMethodResolverFactory           A factory for creating a bridge method resolver.
     * @param classVisitorWrapperChain              A chain of ASM class visitors to apply to the writing process.
     * @param fieldRegistry                         The field registry to apply to the dynamic type creation.
     * @param methodRegistry                        The method registry to apply to the dynamic type creation.
     * @param methodLookupEngineFactory             The method lookup engine factory to apply to the dynamic type creation.
     * @param defaultFieldAttributeAppenderFactory  The field attribute appender factory that should be applied by default if
     *                                              no specific appender was specified for a given field.
     * @param defaultMethodAttributeAppenderFactory The method attribute appender factory that should be applied by default
     *                                              if no specific appender was specified for a given method.
     * @param fieldTokens                           A list of field representations that were added explicitly to this
     *                                              dynamic type.
     * @param methodTokens                          A list of method representations that were added explicitly to this
     *                                              dynamic type.
     * @param classFileLocator                      A locator for finding a class file.
     */
    public FlatDynamicTypeBuilder(ClassFileVersion classFileVersion,
                                  NamingStrategy namingStrategy,
                                  TypeDescription levelType,
                                  List<TypeDescription> interfaceTypes,
                                  int modifiers,
                                  TypeAttributeAppender attributeAppender,
                                  MethodMatcher ignoredMethods,
                                  BridgeMethodResolver.Factory bridgeMethodResolverFactory,
                                  ClassVisitorWrapper.Chain classVisitorWrapperChain,
                                  FieldRegistry fieldRegistry,
                                  MethodRegistry methodRegistry,
                                  MethodLookupEngine.Factory methodLookupEngineFactory,
                                  FieldAttributeAppender.Factory defaultFieldAttributeAppenderFactory,
                                  MethodAttributeAppender.Factory defaultMethodAttributeAppenderFactory,
                                  List<FieldToken> fieldTokens,
                                  List<MethodToken> methodTokens,
                                  ClassFileLocator classFileLocator) {
        super(classFileVersion,
                namingStrategy,
                levelType,
                interfaceTypes,
                modifiers,
                attributeAppender,
                ignoredMethods,
                bridgeMethodResolverFactory,
                classVisitorWrapperChain,
                fieldRegistry, methodRegistry,
                methodLookupEngineFactory,
                defaultFieldAttributeAppenderFactory,
                defaultMethodAttributeAppenderFactory,
                fieldTokens,
                methodTokens);
        this.classFileLocator = classFileLocator;
    }

    @Override
    protected DynamicType.Builder<T> materialize(ClassFileVersion classFileVersion,
                                                 NamingStrategy namingStrategy,
                                                 TypeDescription levelType,
                                                 List<TypeDescription> interfaceTypes,
                                                 int modifiers,
                                                 TypeAttributeAppender attributeAppender,
                                                 MethodMatcher ignoredMethods,
                                                 BridgeMethodResolver.Factory bridgeMethodResolverFactory,
                                                 ClassVisitorWrapper.Chain classVisitorWrapperChain,
                                                 FieldRegistry fieldRegistry,
                                                 MethodRegistry methodRegistry,
                                                 MethodLookupEngine.Factory methodLookupEngineFactory,
                                                 FieldAttributeAppender.Factory defaultFieldAttributeAppenderFactory,
                                                 MethodAttributeAppender.Factory defaultMethodAttributeAppenderFactory,
                                                 List<FieldToken> fieldTokens,
                                                 List<MethodToken> methodTokens) {
        return new FlatDynamicTypeBuilder<T>(classFileVersion,
                namingStrategy,
                levelType,
                interfaceTypes,
                modifiers,
                attributeAppender,
                ignoredMethods,
                bridgeMethodResolverFactory,
                classVisitorWrapperChain,
                fieldRegistry,
                methodRegistry,
                methodLookupEngineFactory,
                defaultFieldAttributeAppenderFactory,
                defaultMethodAttributeAppenderFactory,
                fieldTokens,
                methodTokens,
                classFileLocator);
    }

    @Override
    public DynamicType.Unloaded<T> make() {
        MethodRegistry.Compiled compiledMethodRegistry = methodRegistry.compile(
                applyRecordedMembersTo(new FlatInstrumentedType(classFileVersion,
                        targetType,
                        interfaceTypes,
                        modifiers,
                        namingStrategy)),
                methodLookupEngineFactory.make(classFileVersion),
                new SubclassInstrumentationTarget.Factory(bridgeMethodResolverFactory),
                MethodRegistry.Compiled.Entry.Skip.INSTANCE);
        MethodLookupEngine.Finding finding = compiledMethodRegistry.getFinding();
        TypeExtensionDelegate typeExtensionDelegate = new TypeExtensionDelegate(finding.getTypeDescription(), classFileVersion);
        new TypeWriter.Builder<T>(finding.getTypeDescription(),
                compiledMethodRegistry.getLoadedTypeInitializer(),
                typeExtensionDelegate,
                classFileVersion,
                TypeWriter.Builder.ClassWriterProvider.CleanCopy.INSTANCE);


        return null; // TODO: Implement the type's creation.
    }

    @Override
    public boolean equals(Object other) {
        return this == other || !(other == null || getClass() != other.getClass())
                && super.equals(other)
                && classFileLocator.equals(((FlatDynamicTypeBuilder<?>) other).classFileLocator);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + classFileLocator.hashCode();
    }

    @Override
    public String toString() {
        return "FlatDynamicTypeBuilder{" +
                "classFileVersion=" + classFileVersion +
                ", namingStrategy=" + namingStrategy +
                ", levelType=" + targetType +
                ", interfaceTypes=" + interfaceTypes +
                ", modifiers=" + modifiers +
                ", attributeAppender=" + attributeAppender +
                ", ignoredMethods=" + ignoredMethods +
                ", bridgeMethodResolverFactory=" + bridgeMethodResolverFactory +
                ", classVisitorWrapperChain=" + classVisitorWrapperChain +
                ", fieldRegistry=" + fieldRegistry +
                ", methodRegistry=" + methodRegistry +
                ", methodLookupEngineFactory=" + methodLookupEngineFactory +
                ", defaultFieldAttributeAppenderFactory=" + defaultFieldAttributeAppenderFactory +
                ", defaultMethodAttributeAppenderFactory=" + defaultMethodAttributeAppenderFactory +
                ", classFileLocator=" + classFileLocator +
                '}';
    }
}
