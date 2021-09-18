/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR IMPLIED,
INCLUDING WITHOUT LIMITATION ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE,
MERCHANTABLITY OR NON-INFRINGEMENT.

See the Apache 2 License for the specific language governing permissions and limitations under the License.
*/

package com.msopentech.thali.toronionproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


/**
 * Provides context information about the environment. Implementating classes provide logic for setting up
 * the specific environment
 */
abstract public class OnionProxyContext {


    protected static final Logger LOG = LoggerFactory.getLogger(OnionProxyContext.class);

    /**
     * Tor configuration info used for running and installing tor
     */
    protected final TorConfig config;

    private final Object dataDirLock = new Object();

    private final Object cookieLock = new Object();

    private final Object hostnameLock = new Object();

    /**
     * Constructs instance of <code>OnionProxyContext</code> with specified configDir. Use this constructor when
     * all tor files (including the executable) are under a single directory. Currently, this is used with installers
     * that assemble all necessary files into one location.
     *
     * @param configDir
     * @throws IllegalArgumentException if specified config in null
     */
    public OnionProxyContext(File configDir) {
        this(new TorConfig.Builder(configDir).build());
    }

    /**
     * Constructs instance of <code>OnionProxyContext</code> with the specified torConfig. Typically this constructor
     * will be used when tor is currently installed on the system, with the tor executable and config files in different
     * locations.
     *
     * @param torConfig tor configuration info used for running and installing tor
     * @throws IllegalArgumentException if specified config in null
     */
    public OnionProxyContext(TorConfig torConfig) {
        if (torConfig == null) {
            throw new IllegalArgumentException("torConfig is null");
        }
        this.config = torConfig;
    }


    /**
     * Gets tor configuration info used for running and installing tor
     *
     * @return tor config info
     */
    public final TorConfig getConfig() {
        return config;
    }

    /**
     * Creates the configured tor data directory
     *
     * @return true is directory already exists or has been successfully created, otherwise false
     */
    public final boolean createDataDir() {
        synchronized (dataDirLock) {
            return config.getDataDir().exists() || config.getDataDir().mkdirs();
        }
    }

    /**
     * Deletes the configured tor data directory
     */
    public final void deleteDataDir()  {
        synchronized (dataDirLock) {
            for (File file : config.getDataDir().listFiles()) {
                if (file.isDirectory()) {
                    if (!file.getAbsolutePath().equals(config.getHiddenServiceDir().getAbsolutePath())) {
                        FileUtilities.recursiveFileDelete(file);
                    }
                } else {
                    if (!file.delete()) {
                        throw new RuntimeException("Could not delete file " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Creates an empty cookie auth file
     *
     * @return true if cookie file is created, otherwise false
     */
    public final boolean createCookieAuthFile() {
        synchronized (cookieLock) {
            File cookieAuthFile = config.getCookieAuthFile();
            if (!cookieAuthFile.getParentFile().exists() &&
                    !cookieAuthFile.getParentFile().mkdirs()) {
                LOG.warn("Could not create cookieFile parent directory");
                return false;
            }

            try {
                return (cookieAuthFile.exists() || cookieAuthFile.createNewFile());
            } catch (IOException e) {
                LOG.warn("Could not create cookieFile");
                return false;
            }
        }
    }

    public final boolean createHostnameFile() {
        synchronized (hostnameLock) {
            File hostnameFile = config.getHostnameFile();
            if (!hostnameFile.getParentFile().exists() &&
                    !hostnameFile.getParentFile().mkdirs()) {
                LOG.warn("Could not create hostnameFile parent directory");
                return false;
            }

            try {
                return (hostnameFile.exists() || hostnameFile.createNewFile());
            } catch (IOException e) {
                LOG.warn("Could not create hostnameFile");
                return false;
            }
        }
    }

    /**
     * Creates an observer for the configured cookie auth file
     *
     * @return write observer for cookie auth file
     */
    public final WriteObserver createCookieAuthFileObserver() throws IOException {
        synchronized (cookieLock) {
            return generateWriteObserver(config.getCookieAuthFile());
        }
    }

    /**
     * Creates an observer for the configured hostname file
     *
     * @return write observer for hostname file
     */
    public final WriteObserver createHostnameDirObserver() throws IOException {
        synchronized (hostnameLock) {
            return generateWriteObserver(config.getHostnameFile());
        }
    }

    /**
     * Returns the system process id of the process running this onion proxy
     *
     * @return process id
     */
    public abstract String getProcessId();

    public abstract WriteObserver generateWriteObserver(File file) throws IOException;

    public abstract TorInstaller getInstaller();

}
