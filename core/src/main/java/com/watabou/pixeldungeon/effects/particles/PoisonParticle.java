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
package com.watabou.pixeldungeon.effects.particles;

import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.nikita22007.multiplayer.noosa.particles.Emitter.Factory;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class PoisonParticle extends PixelParticle {
	
	public static final Emitter.Factory MISSILE = new Factory() {
		@Override
		public boolean lightMode() {
			return true;
		}

		@Override
		public String factoryName() {
			return "poison_missile";
		}
	};
	
	public static final Emitter.Factory SPLASH = new Factory() {
		@Override
		public boolean lightMode() {
			return true;
		};
		@Override
		public String factoryName() {
			return "poison_splash";
		}
	};

}