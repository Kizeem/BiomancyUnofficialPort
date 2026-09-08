package com.github.elenterius.biomancy.crafting;

import com.github.elenterius.biomancy.init.ModIngredientTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AnyFoodIngredient implements ICustomIngredient {

	private static final Predicate<FoodProperties> NUTRITION_PREDICATE = foodProperties -> foodProperties != null && foodProperties.nutrition() > 0;

	public static final MapCodec<AnyFoodIngredient> MAP_CODEC = MapCodec.unit(new AnyFoodIngredient());
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyFoodIngredient> STREAM_CODEC = StreamCodec.unit(new AnyFoodIngredient());

	private @Nullable ItemStack[] stacks = null;

	public AnyFoodIngredient() {}

	@Override
	public Stream<ItemStack> getItems() {
		resolve();
		//noinspection ConstantConditions
		return Arrays.stream(stacks);
	}

	private void resolve() {
		if (stacks == null) {
			stacks = BuiltInRegistries.ITEM.stream()
					.map(ItemStack::new)
					.filter(stack -> stack.getFoodProperties(null) != null)
					.filter(stack -> NUTRITION_PREDICATE.test(stack.getFoodProperties(null)))
					.toArray(ItemStack[]::new);
		}
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		if (stack.isEmpty()) return false;
		if (!stack.has(net.minecraft.core.component.DataComponents.FOOD)) return false;

		return NUTRITION_PREDICATE.test(stack.getFoodProperties(null));
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return ModIngredientTypes.ANY_FOOD.get();
	}

}