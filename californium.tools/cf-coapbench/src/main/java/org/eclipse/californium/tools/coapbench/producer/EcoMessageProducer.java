/*******************************************************************************
 * Copyright (c) 2015, 2016 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Martin Lanter - architect and initial implementation
 *    Kai Hudalla (Bosch Software Innovations GmbH) - use static reference to Serializer
 ******************************************************************************/
package org.eclipse.californium.tools.coapbench.producer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.serialization.UdpDataSerializer;
import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.RawData;

/**
 * Produces requests in form of {@link RawData}, i.e. byte arrays. This produces
 * is rather economic because it creates all 65000 requests (with different
 * MIDs) once and only changes the port after all requests have been used once.
 * This producer is able to produce an infinit amount of requests but only needs
 * the memory for 65000 of them.
 */
public class EcoMessageProducer implements Iterator<RawData> {

	private InetAddress address;

	private short[] ports;

	private ArrayList<byte[]> messages;
	private int ptr_message = 0;
	private int ptr_port = 0;
	
	private int counter;
	private int amount;
	private final UdpDataSerializer serializer = new UdpDataSerializer();

	public EcoMessageProducer(String targetURI){
		this(targetURI, Integer.MAX_VALUE);
	}
	
	public EcoMessageProducer(String targetURI, int amount) {
		this.amount = amount;
		try {

			ports = new short[1 << 16];
			ArrayList<Short> ps = new ArrayList<Short>(ports.length);
			for (int i = 0; i < (1 << 16); i++)
				ps.add((short) i);
			Collections.shuffle(ps);
			for (int i = 0; i < (1 << 16); i++)
				ports[i] = ps.get(i);
			Collections.shuffle(Arrays.asList(ports));

			messages = new ArrayList<byte[]>(1 << 16);
			for (int i = 0; i < 1 << 16; i++) {
				Request request = new Request(Code.GET);
				request.setType(Type.NON);
				request.setToken(new byte[0]);
				request.setMID(i);
				request.setURI(targetURI);
				byte[] bytes = serializer.serializeRequest(request).getBytes();
				messages.add(bytes);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		return counter < amount;
	}

	@Override
	public RawData next() {
		RawData raw = RawData.outbound(messages.get(ptr_message), new AddressEndpointContext(address, ports[ptr_port]), null, false);
		if (++ptr_message >= 1 << 16) {
			ptr_message = 0;
			ptr_port++;
		}
		counter++;
		return raw;
	}

	@Override
	public void remove() { }

}
