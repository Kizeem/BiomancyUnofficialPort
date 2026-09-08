package com.github.elenterius.biomancy.crafting;

import com.github.elenterius.biomancy.init.ModIngredientTypes;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import javax.annotation.Nullable;

import java.util.stream.Stream;

public class EssenceIngredient implements ICustomIngredient {

	private final ItemStack itemStack;
	private final CompoundTag partialTag;

	public static final MapCodec<EssenceIngredient> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemStack.STRICT_CODEC.fieldOf("item").forGetter(ingredient -> ingredient.itemStack),
			CompoundTag.CODEC.fieldOf("predicate_tag").forGetter(ingredient -> ingredient.partialTag)
	).apply(instance, EssenceIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EssenceIngredient> STREAM_CODEC = StreamCodec.of(
			(buffer, ingredient) -> {
				ItemStack.STREAM_CODEC.encode(buffer, ingredient.itemStack);
				buffer.writeNbt(ingredient.partialTag);
			},
			buffer -> new EssenceIngredient(ItemStack.STREAM_CODEC.decode(buffer), buffer.readNbt())
	);

	protected EssenceIngredient(ItemStack itemStack, CompoundTag partialTag) {
		this.itemStack = itemStack;
		this.partialTag = partialTag;
	}

	public static EssenceIngredient of(EntityType<?> entityType) {
		return of(entityType, 0);
	}

	public static EssenceIngredient of(EntityType<?> entityType, int tier) {
		if (tier < 0 || tier > 3) throw new IllegalArgumentException("Cannot create a EssenceIngredient with invalid tier");

		CompoundTag essenceTag = new CompoundTag();
		essenceTag.putString(EssenceItem.ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());

		CompoundTag partialTag = new CompoundTag();
		partialTag.put(EssenceItem.ESSENCE_DATA_KEY, essenceTag);
		if (tier > 0) partialTag.putInt(EssenceItem.ESSENCE_TIER_KEY, tier);

		ItemStack stack = EssenceItem.fromEntityType(entityType, tier); //we set the tier here only for visual purposes

		return new EssenceIngredient(stack, partialTag);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		return itemStack.getItem() == stack.getItem();
	}

	@Override
	public Stream<ItemStack> getItems() {
		return Stream.of(itemStack);
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return ModIngredientTypes.ESSENCE.get();
	}

}