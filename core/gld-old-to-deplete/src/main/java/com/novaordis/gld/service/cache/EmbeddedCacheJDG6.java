/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novaordis.gld.service.cache;

import org.apache.log4j.Logger;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.ServerStatistics;
import org.infinispan.client.hotrod.VersionedValue;
// import org.infinispan.commons.util.concurrent.NotifyingFuture;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EmbeddedCacheJDG6 { // implements RemoteCache {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(EmbeddedCacheJDG6.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // RemoteCache implementation --------------------------------------------------------------------------------------



    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "EmbeddedCache[" + "]";
    }

//    @Override
//    public boolean removeWithVersion(Object o, long l) {
//        throw new RuntimeException("removeWithVersion() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> removeWithVersionAsync(Object o, long l) {
//        throw new RuntimeException("removeWithVersionAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean replaceWithVersion(Object o, Object o2, long l) {
//        throw new RuntimeException("replaceWithVersion() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean replaceWithVersion(Object o, Object o2, long l, int i) {
//        throw new RuntimeException("replaceWithVersion() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean replaceWithVersion(Object o, Object o2, long l, int i, int i1) {
//        throw new RuntimeException("replaceWithVersion() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean replaceWithVersion(Object o, Object o2, long l, long l1, TimeUnit timeUnit, long l2, TimeUnit timeUnit1) {
//        throw new RuntimeException("replaceWithVersion() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> replaceWithVersionAsync(Object o, Object o2, long l) {
//        throw new RuntimeException("replaceWithVersionAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> replaceWithVersionAsync(Object o, Object o2, long l, int i) {
//        throw new RuntimeException("replaceWithVersionAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> replaceWithVersionAsync(Object o, Object o2, long l, int i, int i1) {
//        throw new RuntimeException("replaceWithVersionAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public VersionedValue getVersioned(Object o) {
//        throw new RuntimeException("getVersioned() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public MetadataValue getWithMetadata(Object o) {
//        throw new RuntimeException("getWithMetadata() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public int size() {
//        throw new RuntimeException("size() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean isEmpty() {
//        throw new RuntimeException("isEmpty() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//        throw new RuntimeException("containsKey() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean containsValue(Object o) {
//        throw new RuntimeException("containsValue() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object get(Object key) {
//        throw new RuntimeException("get() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Set keySet() {
//        throw new RuntimeException("keySet() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Collection values() {
//        throw new RuntimeException("values() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Set<Entry> entrySet() {
//        throw new RuntimeException("entrySet() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public String getName() {
//        throw new RuntimeException("getName() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public String getVersion() {
//        throw new RuntimeException("getVersion() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object put(Object o, Object o2) {
//        throw new RuntimeException("put() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object put(Object o, Object o2, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("put() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object putIfAbsent(Object o, Object o2, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("putIfAbsent() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void putAll(Map map, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("putAll() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object replace(Object o, Object o2, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("replace() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean replace(Object o, Object o2, Object v1, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("replace() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object put(Object o, Object o2, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("put() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object putIfAbsent(Object o, Object o2, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("putIfAbsent() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void putAll(Map map, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("putAll() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object replace(Object o, Object o2, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("replace() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean replace(Object o, Object o2, Object v1, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("replace() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object remove(Object o) {
//        throw new RuntimeException("remove() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture putAsync(Object o, Object o2) {
//        throw new RuntimeException("putAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture putAsync(Object o, Object o2, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("putAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture putAsync(Object o, Object o2, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("putAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Void> putAllAsync(Map map) {
//        throw new RuntimeException("putAllAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Void> putAllAsync(Map map, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("putAllAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Void> putAllAsync(Map map, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("putAllAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Void> clearAsync() {
//        throw new RuntimeException("clearAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture putIfAbsentAsync(Object o, Object o2) {
//        throw new RuntimeException("putIfAbsentAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture putIfAbsentAsync(Object o, Object o2, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("putIfAbsentAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture putIfAbsentAsync(Object o, Object o2, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("putIfAbsentAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture removeAsync(Object o) {
//        throw new RuntimeException("removeAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> removeAsync(Object o, Object o1) {
//        throw new RuntimeException("removeAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture replaceAsync(Object o, Object o2) {
//        throw new RuntimeException("replaceAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture replaceAsync(Object o, Object o2, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("replaceAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture replaceAsync(Object o, Object o2, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("replaceAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> replaceAsync(Object o, Object o2, Object v1) {
//        throw new RuntimeException("replaceAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> replaceAsync(Object o, Object o2, Object v1, long l, TimeUnit timeUnit) {
//        throw new RuntimeException("replaceAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture<Boolean> replaceAsync(Object o, Object o2, Object v1, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
//        throw new RuntimeException("replaceAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public NotifyingFuture getAsync(Object o) {
//        throw new RuntimeException("getAsync() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void putAll(Map map) {
//        throw new RuntimeException("putAll() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void clear() {
//        throw new RuntimeException("clear() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public ServerStatistics stats() {
//        throw new RuntimeException("stats() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public RemoteCache withFlags(Flag... flags) {
//        throw new RuntimeException("withFlags() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public RemoteCacheManager getRemoteCacheManager() {
//        throw new RuntimeException("getRemoteCacheManager() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Map getBulk() {
//        throw new RuntimeException("getBulk() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Map getBulk(int i) {
//        throw new RuntimeException("getBulk() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Map getAll(Set set) {
//        throw new RuntimeException("getAll() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public String getProtocolVersion() {
//        throw new RuntimeException("getProtocolVersion() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void addClientListener(Object o) {
//        throw new RuntimeException("addClientListener() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void addClientListener(Object o, Object[] objects, Object[] objects1) {
//        throw new RuntimeException("addClientListener() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void removeClientListener(Object o) {
//        throw new RuntimeException("removeClientListener() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Set<Object> getListeners() {
//        throw new RuntimeException("getListeners() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object putIfAbsent(Object key, Object value) {
//        throw new RuntimeException("putIfAbsent() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean remove(Object key, Object value) {
//        throw new RuntimeException("remove() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean replace(Object key, Object oldValue, Object newValue) {
//        throw new RuntimeException("replace() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Object replace(Object key, Object value) {
//        throw new RuntimeException("replace() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void start() {
//        throw new RuntimeException("start() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void stop() {
//        throw new RuntimeException("stop() NOT YET IMPLEMENTED");
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
