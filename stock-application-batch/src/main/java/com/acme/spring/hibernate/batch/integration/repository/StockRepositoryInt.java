/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme.spring.hibernate.batch.integration.repository;

import com.acme.spring.hibernate.batch.integration.domain.StockInt;

/**
 * <p>A stock repository that handles operation in underlying storage.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public interface StockRepositoryInt {

    /**
     * <p>Saves a {@link com.acme.spring.hibernate.batch.integration.domain.StockInt} instance in repository.</p>
     *
     * @param stock the {@link com.acme.spring.hibernate.batch.integration.domain.StockInt} instance
     *
     * @return the identifier of newly created stock
     *
     * @throws IllegalArgumentException if stock is null or if stock's symbol is null or empty string
     */
    String save(StockInt stock);


}
