/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.actors.hero;

import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.HeroHelp;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.KindOfWeapon;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.bags.Bag;
import com.watabou.pixeldungeon.items.bags.Keyring;
import com.watabou.pixeldungeon.items.bags.ScrollHolder;
import com.watabou.pixeldungeon.items.bags.SeedPouch;
import com.watabou.pixeldungeon.items.bags.WandHolster;
import com.watabou.pixeldungeon.items.keys.IronKey;
import com.watabou.pixeldungeon.items.keys.Key;
import com.watabou.pixeldungeon.items.rings.Ring;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.watabou.pixeldungeon.items.wands.Wand;
import com.watabou.pixeldungeon.network.SpecialSlot;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.watabou.pixeldungeon.network.SendData.sendIronKeysCount;

public class Belongings implements Iterable<Item> {

	public static final int BACKPACK_SIZE	= 19;
	
	public Hero owner;
	
	public Bag backpack;	

	public KindOfWeapon weapon = null;
	public Armor armor = null;
	public Ring ring1 = null;
	public Ring ring2 = null;
	public int IronKeyCount_visual = 0; //this is count keys of  this depth. This  is "IronKey.curDepthQuantity"
	public Belongings( Hero owner ) {
		this.owner = owner;
		
		backpack = new Bag() {{
			name = "backpack";
			size = BACKPACK_SIZE;
		}};
		backpack.owner = owner;
	}
	
	private static final String WEAPON		= "weapon";
	private static final String ARMOR		= "armor";
	private static final String RING1		= "ring1";
	private static final String RING2		= "ring2";
	
	public void storeInBundle( Bundle bundle ) {
		
		backpack.storeInBundle( bundle );
		
		bundle.put( WEAPON, weapon );
		bundle.put( ARMOR, armor );
		bundle.put( RING1, ring1 );
		bundle.put( RING2, ring2 );
	}
	
	public void restoreFromBundle( Bundle bundle ) {
		
		backpack.clear();
		backpack.restoreFromBundle( bundle );
		
		weapon = (KindOfWeapon)bundle.get( WEAPON );
		if (weapon != null) {
			weapon.activate( owner );
		}
		
		armor = (Armor)bundle.get( ARMOR );
		
		ring1 = (Ring)bundle.get( RING1 );
		if (ring1 != null) {
			ring1.activate( owner );
		}
		
		ring2 = (Ring)bundle.get( RING2 );
		if (ring2 != null) {
			ring2.activate( owner );
		}
	}

	public ArrayList<SpecialSlot> getSpecialSlots() {
		ArrayList<SpecialSlot> slots = new ArrayList<>(4);
		slots.add(new SpecialSlot(0, "items.png", ItemSpriteSheet.WEAPON, weapon));
		slots.add(new SpecialSlot(1, "items.png", ItemSpriteSheet.ARMOR, armor));
		slots.add(new SpecialSlot(2, "items.png", ItemSpriteSheet.RING, ring1));
		slots.add(new SpecialSlot(3, "items.png", ItemSpriteSheet.RING, ring2));
		return slots;
	}

	public Bag[] getBags() {
        return new Bag[]{
                backpack,
                getItem(SeedPouch.class),
                getItem(ScrollHolder.class),
                getItem(WandHolster.class),
                getItem(Keyring.class)
        };
    }

	public Item getItemInSlot(List<Integer> slot) {
		if (slot.get(0) < 0) {
			SpecialSlot spec_slot = getSpecialSlots().get(-slot.get(0) - 1);
			slot.remove(0);
			if (slot.isEmpty()) {
				return spec_slot.item;
			} else {
				return ((Bag) spec_slot.item).getItemInSlot(slot);
			}
		}
		return backpack.getItemInSlot(slot);
	}

	@SuppressWarnings("unchecked")
	public<T extends Item> T getItem( Class<T> itemClass ) {

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				return (T)item;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Key> T getKey( Class<T> kind, int depth ) {
		
		for (Item item : backpack) {
			if (item.getClass() == kind && ((Key)item).depth == depth) {
				return (T)item;
			}
		}
		
		return null;
	}
	
	public void countIronKeys() {

		int keyscount =0;
		
		for (Item item : backpack) {
			if (item instanceof IronKey && ((IronKey)item).depth == Dungeon.depth) {
				keyscount ++;
			}
		}
		if (keyscount!=IronKeyCount_visual) {
			IronKeyCount_visual=keyscount;
			sendIronKeysCount(HeroHelp.getHeroID(owner), keyscount);
		}
	}
	
	public void identify() {
		for (Item item : this) {
			item.identify();
		}
	}
	
	public void observe() {
		if (weapon != null) {
			weapon.identify();
			Badges.validateItemLevelAquired( weapon );
		}
		if (armor != null) {
			armor.identify();
			Badges.validateItemLevelAquired( armor );
		}
		if (ring1 != null) {
			ring1.identify();
			Badges.validateItemLevelAquired( ring1 );
		}
		if (ring2 != null) {
			ring2.identify();
			Badges.validateItemLevelAquired( ring2 );
		}
		for (Item item : backpack) {
			item.cursedKnown = true;
		}
	}
	
	public void uncurseEquipped() {
		ScrollOfRemoveCurse.uncurse( owner, armor, weapon, ring1, ring2 );
	}
	
	public Item randomUnequipped() {
		return Random.element( backpack.items );
	}
	
	public void resurrect( int depth ) {
		for (Item item : backpack.items.toArray( new Item[0])) {
			if (item instanceof Key) {
				if (((Key)item).depth == depth) {
					item.detachAll( backpack );
				}
			} else if (item.unique) {
				// Keep unique items
			} else if (!item.isEquipped( owner )) {
				item.detachAll( backpack );
			}
		}
		
		if (weapon != null) {
			weapon.cursed = false;
			weapon.activate( owner );
		}
		
		if (armor != null) {
			armor.cursed = false;
		}
		
		if (ring1 != null) {
			ring1.cursed = false;
			ring1.activate( owner );
		}
		if (ring2 != null) {
			ring2.cursed = false;
			ring2.activate( owner );
		}
	}
	
	public int charge( boolean full) {
		
		int count = 0;
		
		for (Item item : this) {
			if (item instanceof Wand) {
				Wand wand = (Wand)item;
				if (wand.curCharges < wand.maxCharges) {
					wand.curCharges = full ? wand.maxCharges : wand.curCharges + 1;
					count++;
					
					wand.updateQuickslot();
				}
			}
		}
		
		return count;
	}
	
	public int discharge() {
		
		int count = 0;
		
		for (Item item : this) {
			if (item instanceof Wand) {
				Wand wand = (Wand)item;
				if (wand.curCharges > 0) {
					wand.curCharges--;
					count++;
					
					wand.updateQuickslot();
				}
			}
		}
		
		return count;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator(); 
	}

	public List<Integer> pathOfItem(@NotNull Item item) {
		assert (item != null) : "path of null item";
		List<SpecialSlot> specialSlots = getSpecialSlots();
		for (int i = 0; i < specialSlots.size(); i++) {
			if (specialSlots.get(i) == null) {
				continue;
			}
			if (specialSlots.get(i).item == item) {
				List<Integer> slot = new ArrayList<>(2);
				slot.add(-i - 1);
				return slot;
			}
			if (specialSlots.get(i).item instanceof Bag) {
				List<Integer> path = ((Bag) specialSlots.get(i).item).pathOfItem(item);
				if (path != null) {
					path.add(0, -i - 1);
					return path;
				}
			}
		}
		return backpack.pathOfItem(item);
	}

	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		
		private Iterator<Item> backpackIterator = backpack.iterator();
		
		private Item[] equipped = {weapon, armor, ring1, ring2};
		private int backpackIndex = equipped.length;
		
		@Override
		public boolean hasNext() {
			
			for (int i=index; i < backpackIndex; i++) {
				if (equipped[i] != null) {
					return true;
				}
			}
			
			return backpackIterator.hasNext();
		}

		@Override
		public Item next() {
			
			while (index < backpackIndex) {
				Item item = equipped[index++];
				if (item != null) {
					return item;
				}
			}
			
			return backpackIterator.next();
		}

		@Override
		public void remove() {
			switch (index) {
			case 0:
				equipped[0] = weapon = null;
				break;
			case 1:
				equipped[1] = armor = null;
				break;
			case 2:
				equipped[2] = ring1 = null;
				break;
			case 3:
				equipped[3] = ring2 = null;
				break;
			default:
				backpackIterator.remove();
			}
		}
	}
}
