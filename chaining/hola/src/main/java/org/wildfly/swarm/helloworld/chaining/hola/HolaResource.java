/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.helloworld.chaining.hola;

import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import io.netty.buffer.ByteBuf;

/**
 * @author Ken Finnigan
 */
@Path("/")
public class HolaResource {

    @GET
    @Path("/hola/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public void sayHello(@PathParam("name") String name, @Suspended AsyncResponse asyncResponse) {
        Future<ByteBuf> future = OlaService.INSTANCE.ola(name).queue();

        try {
            ByteBuf buf = future.get();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            asyncResponse.resume(
                    "Hola " + name + "\n" + new String(bytes)
            );
        } catch (Exception exception) {
            System.err.println("ERROR: " + exception.getLocalizedMessage());
            asyncResponse.resume(exception);
        }
    }
}
