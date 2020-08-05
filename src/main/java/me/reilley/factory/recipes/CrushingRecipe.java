package me.reilley.factory.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import me.reilley.factory.Factory;
import me.reilley.factory.registry.FactoryBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CrushingRecipe implements Recipe<Inventory> {
    protected final Identifier id;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int crushTime;

    public CrushingRecipe(Identifier id, Ingredient input, ItemStack output, int crushTime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.crushTime = crushTime;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return this.input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    public int getCrushTime() {
        return this.crushTime;
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(FactoryBlock.MACERATOR);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Serializer implements RecipeSerializer<CrushingRecipe> {
        public static final Identifier ID = new Identifier(Factory.MOD_ID, "crushing");
        public static final Serializer INSTANCE = new Serializer();

        private static final ImmutableMap<Class<?>, BiFunction<PacketByteBuf, Class<?>, ?>> READERS = ImmutableMap.<Class<?>, BiFunction<PacketByteBuf, Class<?>, ?>> builder().put(Integer.class, (buffer, object) -> buffer.readInt()).put(Float.class, (buffer, object) -> buffer
                .readFloat()).put(Double.class, (buffer, object) -> buffer.readDouble()).put(String.class, (buffer, object) -> buffer.readString()).put(Identifier.class, (buffer, object) -> buffer.readIdentifier()).put(Enum.class, (buffer, object) -> buffer.readEnumConstant(
                ((Enum<?>) (object.getEnumConstants()[0])).getClass())).build();

        private static final ImmutableMap<Class<?>, BiConsumer<PacketByteBuf, Object>> WRITERS = ImmutableMap.<Class<?>, BiConsumer<PacketByteBuf, Object>> builder().put(Integer.class, (buffer, object) -> buffer.writeInt((Integer) object)).put(Float.class, (buffer, object) -> buffer
                .writeFloat((Float) object)).put(Double.class, (buffer, object) -> buffer.writeDouble((Double) object)).put(String.class, (buffer, object) -> buffer.writeString((String) object)).put(Identifier.class, (buffer, object) -> buffer.writeIdentifier((Identifier) object)).put(
                Enum.class, (buffer, object) -> buffer.writeEnumConstant((Enum<?>) object)).build();

        private Serializer() {
        }

        @Override
        public CrushingRecipe read(Identifier identifier, JsonObject object) {
            CrushingRecipe.Format format = new Gson().fromJson(object, CrushingRecipe.Format.class);
            return new CrushingRecipe(identifier, Ingredient.fromJson(format.input), ShapedRecipe.getItemStack(format.output), new Gson().fromJson(format.crushTime, Integer.class));
        }

        @Override
        public CrushingRecipe read(Identifier identifier, PacketByteBuf buffer) {
            return new CrushingRecipe(identifier, Ingredient.fromPacket(buffer), buffer.readItemStack(), (int) readObject(buffer, Integer.class));
        }

        public static Object readObject(PacketByteBuf buffer, Class<?> object) {
            return READERS.get(object).apply(buffer, object);
        }

        @Override
        public void write(PacketByteBuf buffer, CrushingRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            writeObject(buffer, recipe.crushTime);
        }

        public static void writeObject(PacketByteBuf buffer, Object object) {
            WRITERS.get(object.getClass()).accept(buffer, object);
        }
    }

    public static final class Type implements RecipeType<CrushingRecipe> {
        public static final Type INSTANCE = new Type();

        private Type() {
        }
    }

    public static final class Format {
        JsonObject input;
        JsonObject output;
        @SerializedName("crush_time")
        JsonElement crushTime;

        @Override
        public String toString() {
            return "Format{" + "input=" + input + ", output=" + output + ", crushTime=" + crushTime + '}';
        }
    }
}
