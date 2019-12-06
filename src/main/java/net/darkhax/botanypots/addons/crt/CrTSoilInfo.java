package net.darkhax.botanypots.addons.crt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.util.ResourceLocation;

@ZenCodeType.Name("mods.botanypots.Soil")
public class CrTSoilInfo {
    
    private final String id;
    
    private IIngredient ingredient;
    
    private MCBlockState renderState;
    
    private int tickRate;
    
    private Set<String> categories;
    
    private boolean existing = false;
    
    public CrTSoilInfo (SoilInfo info) {
        
        //TODO how to turn Ingredient to IIngredient
        this(info.getId().toString(), null, new MCBlockState(info.getRenderState()), info.getTickRate(), new HashSet<>(Arrays.asList(info.getCategories())));
        this.existing = true;
    }
    
    public CrTSoilInfo (String id) {
        
        this(id, null, null, -1, new HashSet<>());
    }
    
    public CrTSoilInfo(String id, IItemStack ingredient, MCBlockState renderState, int tickRate, Set<String> categories) {
        
        this.id = id;
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.tickRate = tickRate;
        this.categories = categories;
    }
    
    @ZenCodeType.Method
    public String getId () {
        
        return this.id;
    }
    
    @ZenCodeType.Method
    public int getTickRate () {
        
        return this.tickRate;
    }
    
    @ZenCodeType.Method
    public CrTSoilInfo setTickRate (int tickRate) {
        
        this.tickRate = tickRate;
        return this;
    }
    
    @ZenCodeType.Method
    public IIngredient getIngredient () {
        
        return this.ingredient;
    }
    
    @ZenCodeType.Method
    public CrTSoilInfo setIngredient (IIngredient ingredient) {
        
        this.ingredient = ingredient;
        return this;
    }
    
    @ZenCodeType.Method
    public MCBlockState getRenderState () {
        
        return this.renderState;
    }
    
    @ZenCodeType.Method
    public CrTSoilInfo setRenderState (MCBlockState renderState) {
        
        this.renderState = renderState;
        return this;
    }
    
    @ZenCodeType.Method
    public String[] getCategories () {
        
        return this.categories.toArray(new String[0]);
    }

    @ZenCodeType.Method
    public CrTSoilInfo addCategory(String category) {
        
        this.categories.add(category.toLowerCase());
        return this;
    }
    
    @ZenCodeType.Method
    public CrTSoilInfo removeCategory(String category) {
        
        this.categories.remove(category.toLowerCase());
        return this;
    }
    
    @ZenCodeType.Method
    public boolean isExistingSoil() {
        
        return this.existing;
    }
    
    public SoilInfo toSoil() {
        
        // TODO IIngredient to Ingredient
        return new SoilInfo(ResourceLocation.tryCreate(this.getId()), null, this.getRenderState().getInternal(), this.getTickRate(), this.getCategories());
    }
}