/*
 * Copyright (c) 2017 Nova Ordis LLC
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

package io.novaordis.gld.extensions.jboss.datagrid;

import io.novaordis.utilities.NotYetImplementedException;
import org.infinispan.client.hotrod.CacheTopologyInfo;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.ServerStatistics;
import org.infinispan.client.hotrod.VersionedValue;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.commons.util.concurrent.NotifyingFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/11/17
 */
public class MockRemoteCache implements RemoteCache {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;
    private MockRemoteCacheManager manager;
    private boolean started;

    private Map storage;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockRemoteCache(String name, MockRemoteCacheManager manager) {

        this.name = name;
        this.manager = manager;
        this.started = false;
        this.storage = new HashMap<>();
    }

    // RemoteCache implementation --------------------------------------------------------------------------------------

    @Override
    public boolean removeWithVersion(Object key, long version) {

        throw new NotYetImplementedException("removeWithVersion() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> removeWithVersionAsync(Object key, long version) {
        throw new NotYetImplementedException("removeWithVersionAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean replaceWithVersion(Object key, Object newValue, long version) {
        throw new NotYetImplementedException("replaceWithVersion() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean replaceWithVersion(Object key, Object newValue, long version, int lifespanSeconds) {
        throw new NotYetImplementedException("replaceWithVersion() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean replaceWithVersion(Object key, Object newValue, long version, int lifespanSeconds, int maxIdleTimeSeconds) {
        throw new NotYetImplementedException("replaceWithVersion() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean replaceWithVersion(Object key, Object newValue, long version, long lifespan, TimeUnit lifespanTimeUnit, long maxIdle, TimeUnit maxIdleTimeUnit) {
        throw new NotYetImplementedException("replaceWithVersion() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> replaceWithVersionAsync(Object key, Object newValue, long version) {
        throw new NotYetImplementedException("replaceWithVersionAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> replaceWithVersionAsync(Object key, Object newValue, long version, int lifespanSeconds) {
        throw new NotYetImplementedException("replaceWithVersionAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> replaceWithVersionAsync(Object key, Object newValue, long version, int lifespanSeconds, int maxIdleSeconds) {
        throw new NotYetImplementedException("replaceWithVersionAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public CloseableIterator<Entry<Object, Object>> retrieveEntries(String filterConverterFactory, int batchSize) {
        throw new NotYetImplementedException("retrieveEntries() NOT YET IMPLEMENTED");
    }

    @Override
    public VersionedValue getVersioned(Object key) {
        throw new NotYetImplementedException("getVersioned() NOT YET IMPLEMENTED");
    }

    @Override
    public MetadataValue getWithMetadata(Object key) {
        throw new NotYetImplementedException("getWithMetadata() NOT YET IMPLEMENTED");
    }

    @Override
    public int size() {
        throw new NotYetImplementedException("size() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isEmpty() {
        throw new NotYetImplementedException("isEmpty() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new NotYetImplementedException("containsKey() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new NotYetImplementedException("containsValue() NOT YET IMPLEMENTED");
    }

    @Override
    public Object get(Object key) {

        return storage.get(key);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Set keySet() {

        return storage.keySet();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Collection values() {
        throw new NotYetImplementedException("values() NOT YET IMPLEMENTED");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Set<Entry> entrySet() {
        throw new NotYetImplementedException("entrySet() NOT YET IMPLEMENTED");
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getVersion() {
        throw new NotYetImplementedException("getVersion() NOT YET IMPLEMENTED");
    }

    @Override
    public Object put(Object key, Object value) {

        //noinspection unchecked
        return storage.put(key, value);
    }

    @Override
    public Object put(Object key, Object value, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("put() NOT YET IMPLEMENTED");
    }

    @Override
    public Object putIfAbsent(Object key, Object value, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("putIfAbsent() NOT YET IMPLEMENTED");
    }

    @Override
    public void putAll(Map map, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("putAll() NOT YET IMPLEMENTED");
    }

    @Override
    public Object replace(Object key, Object value, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("replace() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean replace(Object key, Object oldValue, Object value, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("replace() NOT YET IMPLEMENTED");
    }

    @Override
    public Object put(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        throw new NotYetImplementedException("put() NOT YET IMPLEMENTED");
    }

    @Override
    public Object putIfAbsent(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        throw new NotYetImplementedException("putIfAbsent() NOT YET IMPLEMENTED");
    }

    @Override
    public void putAll(Map map, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        throw new NotYetImplementedException("putAll() NOT YET IMPLEMENTED");
    }

    @Override
    public Object replace(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        throw new NotYetImplementedException("replace() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean replace(Object key, Object oldValue, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        throw new NotYetImplementedException("replace() NOT YET IMPLEMENTED");
    }

    @Override
    public Object remove(Object key) {

        return storage.remove(key);
    }

    @Override
    public NotifyingFuture putAsync(Object key, Object value) {
        throw new NotYetImplementedException("putAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture putAsync(Object key, Object value, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("putAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture putAsync(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        throw new NotYetImplementedException("putAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Void> putAllAsync(Map data) {
        throw new NotYetImplementedException("putAllAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Void> putAllAsync(Map data, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("putAllAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Void> putAllAsync(Map data, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        throw new NotYetImplementedException("putAllAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Void> clearAsync() {
        throw new NotYetImplementedException("clearAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture putIfAbsentAsync(Object key, Object value) {
        throw new NotYetImplementedException("putIfAbsentAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture putIfAbsentAsync(Object key, Object value, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("putIfAbsentAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture putIfAbsentAsync(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        throw new NotYetImplementedException("putIfAbsentAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture removeAsync(Object key) {
        throw new NotYetImplementedException("removeAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> removeAsync(Object key, Object value) {
        throw new NotYetImplementedException("removeAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture replaceAsync(Object key, Object value) {
        throw new NotYetImplementedException("replaceAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture replaceAsync(Object key, Object value, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("replaceAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture replaceAsync(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        throw new NotYetImplementedException("replaceAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> replaceAsync(Object key, Object oldValue, Object newValue) {
        throw new NotYetImplementedException("replaceAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> replaceAsync(Object key, Object oldValue, Object newValue, long lifespan, TimeUnit unit) {
        throw new NotYetImplementedException("replaceAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture<Boolean> replaceAsync(Object key, Object oldValue, Object newValue, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        throw new NotYetImplementedException("replaceAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public NotifyingFuture getAsync(Object key) {
        throw new NotYetImplementedException("getAsync() NOT YET IMPLEMENTED");
    }

    @Override
    public void putAll(Map m) {
        throw new NotYetImplementedException("putAll() NOT YET IMPLEMENTED");
    }

    @Override
    public void clear() {
        throw new NotYetImplementedException("clear() NOT YET IMPLEMENTED");
    }

    @Override
    public ServerStatistics stats() {
        throw new NotYetImplementedException("stats() NOT YET IMPLEMENTED");
    }

    @Override
    public RemoteCache withFlags(Flag... flags) {
        throw new NotYetImplementedException("withFlags() NOT YET IMPLEMENTED");
    }

    @Override
    public RemoteCacheManager getRemoteCacheManager() {

        return manager;
    }

    @Override
    public Map getBulk() {
        throw new NotYetImplementedException("getBulk() NOT YET IMPLEMENTED");
    }

    @Override
    public Map getBulk(int size) {
        throw new NotYetImplementedException("getBulk() NOT YET IMPLEMENTED");
    }

    @Override
    public Map getAll(Set keys) {
        throw new NotYetImplementedException("getAll() NOT YET IMPLEMENTED");
    }

    @Override
    public String getProtocolVersion() {
        throw new NotYetImplementedException("getProtocolVersion() NOT YET IMPLEMENTED");
    }

    @Override
    public void addClientListener(Object listener) {
        throw new NotYetImplementedException("addClientListener() NOT YET IMPLEMENTED");
    }

    @Override
    public void addClientListener(Object listener, Object[] filterFactoryParams, Object[] converterFactoryParams) {
        throw new NotYetImplementedException("addClientListener() NOT YET IMPLEMENTED");
    }

    @Override
    public void removeClientListener(Object listener) {
        throw new NotYetImplementedException("removeClientListener() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<Object> getListeners() {
        throw new NotYetImplementedException("getListeners() NOT YET IMPLEMENTED");
    }

    @Override
    public CacheTopologyInfo getCacheTopologyInfo() {
        throw new NotYetImplementedException("getCacheTopologyInfo() NOT YET IMPLEMENTED");
    }

    @Override
    public CloseableIterator<Entry<Object, MetadataValue<Object>>> retrieveEntriesWithMetadata(
            Set segments, int batchSize) {
        throw new NotYetImplementedException("retrieveEntriesWithMetadata() NOT YET IMPLEMENTED");
    }

    @Override
    public CloseableIterator<Entry<Object, Object>> retrieveEntries(
            String filterConverterFactory, Object[] filterConverterParams, Set segments, int batchSize) {
        throw new NotYetImplementedException("retrieveEntries() NOT YET IMPLEMENTED");
    }

    @Override
    public CloseableIterator<Entry<Object, Object>> retrieveEntries(
            String filterConverterFactory, Set segments, int batchSize) {
        throw new NotYetImplementedException("retrieveEntries() NOT YET IMPLEMENTED");
    }

    @Override
    public Object putIfAbsent(@SuppressWarnings("NullableProblems") Object key, Object value) {
        throw new NotYetImplementedException("putIfAbsent() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean remove(@SuppressWarnings("NullableProblems") Object key, Object value) {
        throw new NotYetImplementedException("remove() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean replace(@SuppressWarnings("NullableProblems") Object key,
                           @SuppressWarnings("NullableProblems") Object oldValue,
                           @SuppressWarnings("NullableProblems") Object newValue) {
        throw new NotYetImplementedException("replace() NOT YET IMPLEMENTED");
    }

    @Override
    public Object replace(@SuppressWarnings("NullableProblems") Object key,
                          @SuppressWarnings("NullableProblems") Object value) {
        throw new NotYetImplementedException("replace() NOT YET IMPLEMENTED");
    }

    @Override
    public void start() {

        this.started = true;
    }

    @Override
    public void stop() {

        this.started = false;
    }

    @Override
    public String toString() {

        return "MockRemoteCache[" + getName() + "]";
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public boolean isStarted() {

        return started;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    void setRemoteCacheManager(MockRemoteCacheManager mcm) {

        this.manager = mcm;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
