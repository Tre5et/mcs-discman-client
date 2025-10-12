package net.treset.discmanextras.wrapper;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.dedicated.management.UriUtil;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.server.dedicated.management.schema.RpcSchemaEntry;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class RecordSchemaBuilder<T> {
    protected String name;

    protected abstract List<SchemaData<T,?>> propertiesList();

    protected SchemaWrapper<T> buildInternal(Function<RecordCodecBuilder.Instance<T>, App<RecordCodecBuilder.Mu<T>, T>> createInstance) {
        Codec<T> codec = RecordCodecBuilder.create(createInstance);

        RpcSchema schema = RpcSchema.ofObject();
        for(SchemaData<T,?> p : propertiesList()) {
            schema = p.applyToSchema(schema);
        }

        RpcSchemaEntry schemaEntry = new RpcSchemaEntry(
                name,
                UriUtil.createSchemasUri(name),
                schema
        );

        return new SchemaWrapper<>(codec, schemaEntry);
    }

    public static class RecordSchemaBuilder0<T> extends RecordSchemaBuilder<T> {
        public RecordSchemaBuilder0(String name) {
            this.name = name;
        }

        public <T1> RecordSchemaBuilder1<T,T1> property(SchemaData<T,T1> data) {
            return new RecordSchemaBuilder1<>(this, data);
        }

        public <T1> RecordSchemaBuilder1<T,T1> property(String name, Codec<T1> codec, RpcSchema schema, Function<T,T1> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T1> RecordSchemaBuilder1<T,T1> property(String name, SchemaWrapper<T1> wrapper, Function<T,T1> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            throw new IllegalStateException("No properties added, cannot list them");
        }
    }

    public static class RecordSchemaBuilder1<T,T1> extends RecordSchemaBuilder<T> {
        private SchemaData<T,T1> p1;

        public RecordSchemaBuilder1(RecordSchemaBuilder0<T> builder, SchemaData<T,T1> p1) {
            this.name = builder.name;
            this.p1 = p1;
        }

        public <T2> RecordSchemaBuilder2<T,T1,T2> property(SchemaData<T,T2> data) {
            return new RecordSchemaBuilder2<>(this, data);
        }

        public <T2> RecordSchemaBuilder2<T,T1,T2> property(String name, Codec<T2> codec, RpcSchema schema, Function<T,T2> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T2> RecordSchemaBuilder2<T,T1,T2> property(String name, SchemaWrapper<T2> wrapper, Function<T,T2> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T2> RecordSchemaBuilder2<T,T1,Optional<T2>> optionalProperty(String name, Codec<T2> codec, RpcSchema schema, Function<T,Optional<T2>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T2> RecordSchemaBuilder2<T,T1,Optional<T2>> optionalProperty(String name, SchemaWrapper<T2> wrapper, Function<T,Optional<T2>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1);
        }

        public SchemaWrapper<T> build(Function<T1,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }

    public static class RecordSchemaBuilder2<T,T1,T2> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;

        public RecordSchemaBuilder2(RecordSchemaBuilder1<T,T1> builder, SchemaData<T,T2> p2) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = p2;
        }

        public <T3> RecordSchemaBuilder3<T,T1,T2,T3> property(SchemaData<T,T3> data) {
            return new RecordSchemaBuilder3<>(this, data);
        }

        public <T3> RecordSchemaBuilder3<T,T1,T2,T3> property(String name, Codec<T3> codec, RpcSchema schema, Function<T,T3> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T3> RecordSchemaBuilder3<T,T1,T2,T3> property(String name, SchemaWrapper<T3> wrapper, Function<T,T3> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T3> RecordSchemaBuilder3<T,T1,T2,Optional<T3>> optionalProperty(String name, Codec<T3> codec, RpcSchema schema, Function<T,Optional<T3>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T3> RecordSchemaBuilder3<T,T1,T2,Optional<T3>> optionalProperty(String name, SchemaWrapper<T3> wrapper, Function<T,Optional<T3>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2);
        }

        public SchemaWrapper<T> build(BiFunction<T1,T2,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder3<T,T1,T2,T3> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;

        public RecordSchemaBuilder3(RecordSchemaBuilder2<T,T1,T2> builder, SchemaData<T,T3> p3) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = p3;
        }

        public <T4> RecordSchemaBuilder4<T,T1,T2,T3,T4> property(SchemaData<T,T4> data) {
            return new RecordSchemaBuilder4<>(this, data);
        }

        public <T4> RecordSchemaBuilder4<T,T1,T2,T3,T4> property(String name, Codec<T4> codec, RpcSchema schema, Function<T,T4> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T4> RecordSchemaBuilder4<T,T1,T2,T3,T4> property(String name, SchemaWrapper<T4> wrapper, Function<T,T4> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T4> RecordSchemaBuilder4<T,T1,T2,T3,Optional<T4>> optionalProperty(String name, Codec<T4> codec, RpcSchema schema, Function<T,Optional<T4>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T4> RecordSchemaBuilder4<T,T1,T2,T3,Optional<T4>> optionalProperty(String name, SchemaWrapper<T4> wrapper, Function<T,Optional<T4>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3);
        }

        public SchemaWrapper<T> build(Function3<T1,T2,T3,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder4<T,T1,T2,T3,T4> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;

        public RecordSchemaBuilder4(RecordSchemaBuilder3<T,T1,T2,T3> builder, SchemaData<T,T4> p4) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = p4;
        }

        public <T5> RecordSchemaBuilder5<T,T1,T2,T3,T4,T5> property(SchemaData<T,T5> data) {
            return new RecordSchemaBuilder5<>(this, data);
        }

        public <T5> RecordSchemaBuilder5<T,T1,T2,T3,T4,T5> property(String name, Codec<T5> codec, RpcSchema schema, Function<T,T5> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T5> RecordSchemaBuilder5<T,T1,T2,T3,T4,T5> property(String name, SchemaWrapper<T5> wrapper, Function<T,T5> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T5> RecordSchemaBuilder5<T,T1,T2,T3,T4,Optional<T5>> optionalProperty(String name, Codec<T5> codec, RpcSchema schema, Function<T,Optional<T5>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T5> RecordSchemaBuilder5<T,T1,T2,T3,T4,Optional<T5>> optionalProperty(String name, SchemaWrapper<T5> wrapper, Function<T,Optional<T5>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4);
        }

        public SchemaWrapper<T> build(Function4<T1,T2,T3,T4,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder5<T,T1,T2,T3,T4,T5> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;

        public RecordSchemaBuilder5(RecordSchemaBuilder4<T,T1,T2,T3,T4> builder, SchemaData<T,T5> p5) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = p5;
        }

        public <T6> RecordSchemaBuilder6<T,T1,T2,T3,T4,T5,T6> property(SchemaData<T,T6> data) {
            return new RecordSchemaBuilder6<>(this, data);
        }

        public <T6> RecordSchemaBuilder6<T,T1,T2,T3,T4,T5,T6> property(String name, Codec<T6> codec, RpcSchema schema, Function<T,T6> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T6> RecordSchemaBuilder6<T,T1,T2,T3,T4,T5,T6> property(String name, SchemaWrapper<T6> wrapper, Function<T,T6> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T6> RecordSchemaBuilder6<T,T1,T2,T3,T4,T5,Optional<T6>> optionalProperty(String name, Codec<T6> codec, RpcSchema schema, Function<T,Optional<T6>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T6> RecordSchemaBuilder6<T,T1,T2,T3,T4,T5,Optional<T6>> optionalProperty(String name, SchemaWrapper<T6> wrapper, Function<T,Optional<T6>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5);
        }

        public SchemaWrapper<T> build(Function5<T1,T2,T3,T4,T5,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder6<T,T1,T2,T3,T4,T5,T6> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;

        public RecordSchemaBuilder6(RecordSchemaBuilder5<T,T1,T2,T3,T4,T5> builder, SchemaData<T,T6> p6) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = p6;
        }

        public <T7> RecordSchemaBuilder7<T,T1,T2,T3,T4,T5,T6,T7> property(SchemaData<T,T7> data) {
            return new RecordSchemaBuilder7<>(this, data);
        }

        public <T7> RecordSchemaBuilder7<T,T1,T2,T3,T4,T5,T6,T7> property(String name, Codec<T7> codec, RpcSchema schema, Function<T,T7> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T7> RecordSchemaBuilder7<T,T1,T2,T3,T4,T5,T6,T7> property(String name, SchemaWrapper<T7> wrapper, Function<T,T7> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T7> RecordSchemaBuilder7<T,T1,T2,T3,T4,T5,T6,Optional<T7>> optionalProperty(String name, Codec<T7> codec, RpcSchema schema, Function<T,Optional<T7>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T7> RecordSchemaBuilder7<T,T1,T2,T3,T4,T5,T6,Optional<T7>> optionalProperty(String name, SchemaWrapper<T7> wrapper, Function<T,Optional<T7>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6);
        }

        public SchemaWrapper<T> build(Function6<T1,T2,T3,T4,T5,T6,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder7<T,T1,T2,T3,T4,T5,T6,T7> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;

        public RecordSchemaBuilder7(RecordSchemaBuilder6<T,T1,T2,T3,T4,T5,T6> builder, SchemaData<T,T7> p7) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = p7;
        }

        public <T8> RecordSchemaBuilder8<T,T1,T2,T3,T4,T5,T6,T7,T8> property(SchemaData<T,T8> data) {
            return new RecordSchemaBuilder8<>(this, data);
        }

        public <T8> RecordSchemaBuilder8<T,T1,T2,T3,T4,T5,T6,T7,T8> property(String name, Codec<T8> codec, RpcSchema schema, Function<T,T8> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T8> RecordSchemaBuilder8<T,T1,T2,T3,T4,T5,T6,T7,T8> property(String name, SchemaWrapper<T8> wrapper, Function<T,T8> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T8> RecordSchemaBuilder8<T,T1,T2,T3,T4,T5,T6,T7,Optional<T8>> optionalProperty(String name, Codec<T8> codec, RpcSchema schema, Function<T,Optional<T8>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T8> RecordSchemaBuilder8<T,T1,T2,T3,T4,T5,T6,T7,Optional<T8>> optionalProperty(String name, SchemaWrapper<T8> wrapper, Function<T,Optional<T8>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7);
        }

        public SchemaWrapper<T> build(Function7<T1,T2,T3,T4,T5,T6,T7,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder8<T,T1,T2,T3,T4,T5,T6,T7,T8> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;

        public RecordSchemaBuilder8(RecordSchemaBuilder7<T,T1,T2,T3,T4,T5,T6,T7> builder, SchemaData<T,T8> p8) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = p8;
        }

        public <T9> RecordSchemaBuilder9<T,T1,T2,T3,T4,T5,T6,T7,T8,T9> property(SchemaData<T,T9> data) {
            return new RecordSchemaBuilder9<>(this, data);
        }

        public <T9> RecordSchemaBuilder9<T,T1,T2,T3,T4,T5,T6,T7,T8,T9> property(String name, Codec<T9> codec, RpcSchema schema, Function<T,T9> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T9> RecordSchemaBuilder9<T,T1,T2,T3,T4,T5,T6,T7,T8,T9> property(String name, SchemaWrapper<T9> wrapper, Function<T,T9> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T9> RecordSchemaBuilder9<T,T1,T2,T3,T4,T5,T6,T7,T8,Optional<T9>> optionalProperty(String name, Codec<T9> codec, RpcSchema schema, Function<T,Optional<T9>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T9> RecordSchemaBuilder9<T,T1,T2,T3,T4,T5,T6,T7,T8,Optional<T9>> optionalProperty(String name, SchemaWrapper<T9> wrapper, Function<T,Optional<T9>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8);
        }

        public SchemaWrapper<T> build(Function8<T1,T2,T3,T4,T5,T6,T7,T8,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder9<T,T1,T2,T3,T4,T5,T6,T7,T8,T9> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;

        public RecordSchemaBuilder9(RecordSchemaBuilder8<T,T1,T2,T3,T4,T5,T6,T7,T8> builder, SchemaData<T,T9> p9) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = p9;
        }

        public <T10> RecordSchemaBuilder10<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> property(SchemaData<T,T10> data) {
            return new RecordSchemaBuilder10<>(this, data);
        }

        public <T10> RecordSchemaBuilder10<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> property(String name, Codec<T10> codec, RpcSchema schema, Function<T,T10> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T10> RecordSchemaBuilder10<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> property(String name, SchemaWrapper<T10> wrapper, Function<T,T10> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T10> RecordSchemaBuilder10<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,Optional<T10>> optionalProperty(String name, Codec<T10> codec, RpcSchema schema, Function<T,Optional<T10>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T10> RecordSchemaBuilder10<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,Optional<T10>> optionalProperty(String name, SchemaWrapper<T10> wrapper, Function<T,Optional<T10>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9);
        }

        public SchemaWrapper<T> build(Function9<T1,T2,T3,T4,T5,T6,T7,T8,T9,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder10<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;
        private final SchemaData<T,T10> p10;

        public RecordSchemaBuilder10(RecordSchemaBuilder9<T,T1,T2,T3,T4,T5,T6,T7,T8,T9> builder, SchemaData<T,T10> p10) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = builder.p9;
            this.p10 = p10;
        }

        public <T11> RecordSchemaBuilder11<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> property(SchemaData<T,T11> data) {
            return new RecordSchemaBuilder11<>(this, data);
        }

        public <T11> RecordSchemaBuilder11<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> property(String name, Codec<T11> codec, RpcSchema schema, Function<T,T11> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T11> RecordSchemaBuilder11<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> property(String name, SchemaWrapper<T11> wrapper, Function<T,T11> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T11> RecordSchemaBuilder11<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Optional<T11>> optionalProperty(String name, Codec<T11> codec, RpcSchema schema, Function<T,Optional<T11>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T11> RecordSchemaBuilder11<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Optional<T11>> optionalProperty(String name, SchemaWrapper<T11> wrapper, Function<T,Optional<T11>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10);
        }

        public SchemaWrapper<T> build(Function10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder(),
                            p10.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder11<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;
        private final SchemaData<T,T10> p10;
        private final SchemaData<T,T11> p11;

        public RecordSchemaBuilder11(RecordSchemaBuilder10<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> builder, SchemaData<T,T11> p11) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = builder.p9;
            this.p10 = builder.p10;
            this.p11 = p11;
        }

        public <T12> RecordSchemaBuilder12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> property(SchemaData<T,T12> data) {
            return new RecordSchemaBuilder12<>(this, data);
        }

        public <T12> RecordSchemaBuilder12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> property(String name, Codec<T12> codec, RpcSchema schema, Function<T,T12> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T12> RecordSchemaBuilder12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> property(String name, SchemaWrapper<T12> wrapper, Function<T,T12> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T12> RecordSchemaBuilder12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Optional<T12>> optionalProperty(String name, Codec<T12> codec, RpcSchema schema, Function<T,Optional<T12>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T12> RecordSchemaBuilder12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Optional<T12>> optionalProperty(String name, SchemaWrapper<T12> wrapper, Function<T,Optional<T12>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11);
        }

        public SchemaWrapper<T> build(Function11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder(),
                            p10.codecBuilder(),
                            p11.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;
        private final SchemaData<T,T10> p10;
        private final SchemaData<T,T11> p11;
        private final SchemaData<T,T12> p12;

        public RecordSchemaBuilder12(RecordSchemaBuilder11<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> builder, SchemaData<T,T12> p12) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = builder.p9;
            this.p10 = builder.p10;
            this.p11 = builder.p11;
            this.p12 = p12;
        }

        public <T13> RecordSchemaBuilder13<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> property(SchemaData<T,T13> data) {
            return new RecordSchemaBuilder13<>(this, data);
        }

        public <T13> RecordSchemaBuilder13<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> property(String name, Codec<T13> codec, RpcSchema schema, Function<T,T13> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T13> RecordSchemaBuilder13<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> property(String name, SchemaWrapper<T13> wrapper, Function<T,T13> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T13> RecordSchemaBuilder13<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Optional<T13>> optionalProperty(String name, Codec<T13> codec, RpcSchema schema, Function<T,Optional<T13>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T13> RecordSchemaBuilder13<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Optional<T13>> optionalProperty(String name, SchemaWrapper<T13> wrapper, Function<T,Optional<T13>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12);
        }

        public SchemaWrapper<T> build(Function12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder(),
                            p10.codecBuilder(),
                            p11.codecBuilder(),
                            p12.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder13<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;
        private final SchemaData<T,T10> p10;
        private final SchemaData<T,T11> p11;
        private final SchemaData<T,T12> p12;
        private final SchemaData<T,T13> p13;

        public RecordSchemaBuilder13(RecordSchemaBuilder12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> builder, SchemaData<T,T13> p13) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = builder.p9;
            this.p10 = builder.p10;
            this.p11 = builder.p11;
            this.p12 = builder.p12;
            this.p13 = p13;
        }

        public <T14> RecordSchemaBuilder14<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> property(SchemaData<T,T14> data) {
            return new RecordSchemaBuilder14<>(this, data);
        }

        public <T14> RecordSchemaBuilder14<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> property(String name, Codec<T14> codec, RpcSchema schema, Function<T,T14> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T14> RecordSchemaBuilder14<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> property(String name, SchemaWrapper<T14> wrapper, Function<T,T14> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T14> RecordSchemaBuilder14<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Optional<T14>> optionalProperty(String name, Codec<T14> codec, RpcSchema schema, Function<T,Optional<T14>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T14> RecordSchemaBuilder14<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Optional<T14>> optionalProperty(String name, SchemaWrapper<T14> wrapper, Function<T,Optional<T14>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13);
        }

        public SchemaWrapper<T> build(Function13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder(),
                            p10.codecBuilder(),
                            p11.codecBuilder(),
                            p12.codecBuilder(),
                            p13.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder14<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;
        private final SchemaData<T,T10> p10;
        private final SchemaData<T,T11> p11;
        private final SchemaData<T,T12> p12;
        private final SchemaData<T,T13> p13;
        private final SchemaData<T,T14> p14;

        public RecordSchemaBuilder14(RecordSchemaBuilder13<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> builder, SchemaData<T,T14> p14) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = builder.p9;
            this.p10 = builder.p10;
            this.p11 = builder.p11;
            this.p12 = builder.p12;
            this.p13 = builder.p13;
            this.p14 = p14;
        }

        public <T15> RecordSchemaBuilder15<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> property(SchemaData<T,T15> data) {
            return new RecordSchemaBuilder15<>(this, data);
        }

        public <T15> RecordSchemaBuilder15<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> property(String name, Codec<T15> codec, RpcSchema schema, Function<T,T15> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T15> RecordSchemaBuilder15<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> property(String name, SchemaWrapper<T15> wrapper, Function<T,T15> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T15> RecordSchemaBuilder15<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Optional<T15>> optionalProperty(String name, Codec<T15> codec, RpcSchema schema, Function<T,Optional<T15>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T15> RecordSchemaBuilder15<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Optional<T15>> optionalProperty(String name, SchemaWrapper<T15> wrapper, Function<T,Optional<T15>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14);
        }

        public SchemaWrapper<T> build(Function14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder(),
                            p10.codecBuilder(),
                            p11.codecBuilder(),
                            p12.codecBuilder(),
                            p13.codecBuilder(),
                            p14.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder15<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;
        private final SchemaData<T,T10> p10;
        private final SchemaData<T,T11> p11;
        private final SchemaData<T,T12> p12;
        private final SchemaData<T,T13> p13;
        private final SchemaData<T,T14> p14;
        private final SchemaData<T,T15> p15;

        public RecordSchemaBuilder15(RecordSchemaBuilder14<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> builder, SchemaData<T,T15> p15) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = builder.p9;
            this.p10 = builder.p10;
            this.p11 = builder.p11;
            this.p12 = builder.p12;
            this.p13 = builder.p13;
            this.p14 = builder.p14;
            this.p15 = p15;
        }

        public <T16> RecordSchemaBuilder16<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> property(SchemaData<T,T16> data) {
            return new RecordSchemaBuilder16<>(this, data);
        }

        public <T16> RecordSchemaBuilder16<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> property(String name, Codec<T16> codec, RpcSchema schema, Function<T,T16> getter) {
            return property(SchemaData.of(name, codec, schema, getter));
        }

        public <T16> RecordSchemaBuilder16<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> property(String name, SchemaWrapper<T16> wrapper, Function<T,T16> getter) {
            return property(SchemaData.of(name, wrapper, getter));
        }

        public <T16> RecordSchemaBuilder16<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Optional<T16>> optionalProperty(String name, Codec<T16> codec, RpcSchema schema, Function<T,Optional<T16>> getter) {
            return property(SchemaData.ofOptional(name, codec, schema, getter));
        }

        public <T16> RecordSchemaBuilder16<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Optional<T16>> optionalProperty(String name, SchemaWrapper<T16> wrapper, Function<T,Optional<T16>> getter) {
            return property(SchemaData.ofOptional(name, wrapper, getter));
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15);
        }

        public SchemaWrapper<T> build(Function15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder(),
                            p10.codecBuilder(),
                            p11.codecBuilder(),
                            p12.codecBuilder(),
                            p13.codecBuilder(),
                            p14.codecBuilder(),
                            p15.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
    public static class RecordSchemaBuilder16<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> extends RecordSchemaBuilder<T> {
        private final SchemaData<T,T1> p1;
        private final SchemaData<T,T2> p2;
        private final SchemaData<T,T3> p3;
        private final SchemaData<T,T4> p4;
        private final SchemaData<T,T5> p5;
        private final SchemaData<T,T6> p6;
        private final SchemaData<T,T7> p7;
        private final SchemaData<T,T8> p8;
        private final SchemaData<T,T9> p9;
        private final SchemaData<T,T10> p10;
        private final SchemaData<T,T11> p11;
        private final SchemaData<T,T12> p12;
        private final SchemaData<T,T13> p13;
        private final SchemaData<T,T14> p14;
        private final SchemaData<T,T15> p15;
        private final SchemaData<T,T16> p16;

        public RecordSchemaBuilder16(RecordSchemaBuilder15<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> builder, SchemaData<T,T16> p16) {
            this.name = builder.name;
            this.p1 = builder.p1;
            this.p2 = builder.p2;
            this.p3 = builder.p3;
            this.p4 = builder.p4;
            this.p5 = builder.p5;
            this.p6 = builder.p6;
            this.p7 = builder.p7;
            this.p8 = builder.p8;
            this.p9 = builder.p9;
            this.p10 = builder.p10;
            this.p11 = builder.p11;
            this.p12 = builder.p12;
            this.p13 = builder.p13;
            this.p14 = builder.p14;
            this.p15 = builder.p15;
            this.p16 = p16;
        }

        @Override
        protected List<SchemaData<T,?>> propertiesList() {
            return List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16);
        }

        public SchemaWrapper<T> build(Function16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T> applicator) {
            return buildInternal(
                    i -> i.group(
                            p1.codecBuilder(),
                            p2.codecBuilder(),
                            p3.codecBuilder(),
                            p4.codecBuilder(),
                            p5.codecBuilder(),
                            p6.codecBuilder(),
                            p7.codecBuilder(),
                            p8.codecBuilder(),
                            p9.codecBuilder(),
                            p10.codecBuilder(),
                            p11.codecBuilder(),
                            p12.codecBuilder(),
                            p13.codecBuilder(),
                            p14.codecBuilder(),
                            p15.codecBuilder(),
                            p16.codecBuilder()
                    ).apply(i, applicator)
            );
        }
    }
}