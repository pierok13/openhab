/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2011, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.binding.ntp.internal;

import java.util.Locale;
import java.util.TimeZone;

import org.openhab.binding.ntp.NtpBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.StringItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * <p>This class can parse information from the generic binding format and 
 * provides NTP binding information from it. It registers as a 
 * {@link NtpBindingProvider} service as well.</p>
 * 
 * <p>Here are some examples for valid binding configuration strings:
 * <ul>
 * 	<li><code>{ ntp="DATE:Central European Summer Time:de_DE" }</code>
 * 	<li><code>{ ntp="TIME:Central European Summer Time" }</code>
 * 	<li><code>{ ntp="TIME:America/Los_Angeles" }</code>
 * 	<li><code>{ ntp="DATE" }</code>
 * </ul>
 * 
 * @author Thomas.Eichstaedt-Engelen
 * 
 * @since 0.7.0
 */
public class NtpGenericBindingProvider extends AbstractGenericBindingProvider implements NtpBindingProvider {

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "ntp";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		
		super.processBindingConfiguration(context, item, bindingConfig);
		
		if (item instanceof StringItem) {
			String[] configParts = bindingConfig.trim().split(":");
			if (configParts.length > 3) {
				throw new BindingConfigParseException("NTP binding configuration must not contain more than three parts");
			}
			
			NTPBindingConfig config = new NTPBindingConfig();
			
			item.getName();
			
			try {
				config.modus = DateTimeModus.valueOf(configParts[0]);
			}
			catch (IllegalArgumentException iae) {
				throw new BindingConfigParseException("couldn't instantiate DateTimeModus '" + configParts[0] + "'");
			}
			
			config.timeZone = configParts.length > 1 ? TimeZone.getTimeZone(configParts[1]) : TimeZone.getDefault();
			config.locale = configParts.length > 2 ? new Locale(configParts[2]) : Locale.getDefault();

			addBindingConfig(item, config);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public DateTimeModus getModus(String itemName) {
		NTPBindingConfig config = (NTPBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.modus : DateTimeModus.DATE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public TimeZone getTimeZone(String itemName) {
		NTPBindingConfig config = (NTPBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.timeZone : TimeZone.getDefault();
	}

	/**
	 * {@inheritDoc}
	 */
	public Locale getLocale(String itemName) {
		NTPBindingConfig config = (NTPBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.locale : Locale.getDefault();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Iterable<String> getItemNames() {
		return bindingConfigs.keySet();
	}	
	
	
	/**
	 * This is an internal data structure to store information from the binding
	 * config strings and use it to answer the requests to the NTP
	 * binding provider.
	 */
	static private class NTPBindingConfig implements BindingConfig {
		public DateTimeModus modus;
		public TimeZone timeZone;
		public Locale locale;
	}


}