/*
 * Copyright 2010-2012 JetBrains s.r.o.
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

package org.jetbrains.jet.plugin.stubindex;

import com.intellij.psi.stubs.IndexSink;
import org.jetbrains.jet.lang.psi.stubs.*;
import org.jetbrains.jet.lang.psi.stubs.elements.StubIndexService;
import org.jetbrains.jet.lang.resolve.name.FqName;

/**
 * @author Nikolay Krasko
 */
public class StubIndexServiceImpl implements StubIndexService {

    @Override
    public void indexFile(PsiJetFileStub stub, IndexSink sink) {
        String packageName = stub.getPackageName();
        sink.occurrence(JetPackageDeclarationIndex.getInstance().getKey(), packageName == null ? "" : packageName);
    }

    @Override
    public void indexClass(PsiJetClassStub stub, IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(JetShortClassNameIndex.getInstance().getKey(), name);
        }
        
        String fqn = stub.getQualifiedName();
        if (fqn != null) {
            sink.occurrence(JetFullClassNameIndex.getInstance().getKey(), fqn);
        }

        for (String superName : stub.getSuperNames()) {
            sink.occurrence(JetSuperClassIndex.getInstance().getKey(), superName);
        }
    }

    @Override
    public void indexObject(PsiJetObjectStub stub, IndexSink sink) {
        String name = stub.getName();
        assert name != null;

        sink.occurrence(JetShortClassNameIndex.getInstance().getKey(), name);

        if (stub.isTopLevel()) {
            sink.occurrence(JetTopLevelShortObjectNameIndex.getInstance().getKey(), name);
        }

        FqName fqName = stub.getFQName();
        if (fqName != null) {
            sink.occurrence(JetFullClassNameIndex.getInstance().getKey(), fqName.getFqName());
        }
    }

    @Override
    public void indexFunction(PsiJetFunctionStub stub, IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            if (stub.isTopLevel()) {
                // Collection only top level functions as only they are expected in completion without explicit import
                if (!stub.isExtension()) {
                    sink.occurrence(JetShortFunctionNameIndex.getInstance().getKey(), name);
                }
                else {
                    sink.occurrence(JetExtensionFunctionNameIndex.getInstance().getKey(), name);
                }

                FqName topFQName = stub.getTopFQName();
                if (topFQName != null) {
                    sink.occurrence(JetTopLevelFunctionsFqnNameIndex.getInstance().getKey(), topFQName.getFqName());
                }
            }

            sink.occurrence(JetAllShortFunctionNameIndex.getInstance().getKey(), name);
        }
    }

    @Override
    public void indexProperty(PsiJetPropertyStub stub, IndexSink sink) {
        String propertyName = stub.getName();
        if (propertyName != null) {
            if (stub.isTopLevel()) {
                FqName topFQName = stub.getTopFQName();
                if (topFQName != null) {
                    sink.occurrence(JetTopLevelPropertiesFqnNameIndex.getInstance().getKey(), topFQName.getFqName());
                }
            }

            sink.occurrence(JetShortPropertiesNameIndex.getInstance().getKey(), propertyName);
        }
    }

    @Override
    public void indexAnnotation(PsiJetAnnotationStub stub, IndexSink sink) {
        sink.occurrence(JetAnnotationsIndex.getInstance().getKey(), stub.getShortName());
    }
}
