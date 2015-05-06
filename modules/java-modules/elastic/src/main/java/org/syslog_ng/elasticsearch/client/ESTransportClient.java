/*
 * Copyright (c) 2015 BalaBit IT Ltd, Budapest, Hungary
 * Copyright (c) 2015 Viktor Juhasz <viktor.juhasz@balabit.com>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As an additional exemption you are allowed to compile & link against the
 * OpenSSL libraries as published by the OpenSSL project. See the file
 * COPYING for details.
 *
 */

package org.syslog_ng.elasticsearch.client;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.syslog_ng.elasticsearch.options.ElasticSearchOptions;

public class ESTransportClient extends ESClient {
	private Settings settings;
	private TransportClient transport;
	
	public ESTransportClient(ElasticSearchOptions options) {
		super(options);
	}
	
	public Client createClient() {
		settings = ImmutableSettings.settingsBuilder()
				.put("client.transport.sniff", true)
				.put("cluster.name", options.getCluster())
				.classLoader(Settings.class.getClassLoader()).build();
		
		String[] servers =  options.getServerList();

		transport = new TransportClient(settings);

		for (String server: servers) {
			transport.addTransportAddress(new InetSocketTransportAddress(server,
					options.getPort()));
		}
		return transport;
	}

	public void close() {
		getClient().close();
		resetClient();
	}

	@Override
	public boolean isOpened() {
		return !transport.connectedNodes().isEmpty();
	}

	@Override
	public void deinit() {
		
	}
}
