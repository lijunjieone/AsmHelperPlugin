package com.bn.simpleplugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.kronos.plugin.base.BaseTransform
import com.kronos.plugin.base.ClassUtils
import com.kronos.plugin.base.TransformCallBack
import org.gradle.api.Project
import java.io.IOException

class SimpleTransform(private val project: Project):Transform() {
    override fun getName(): String {
        return "SimpleTransform"
    }


    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_JARS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    @Throws(TransformException::class, InterruptedException::class, IOException::class)
    override fun transform(transformInvocation: TransformInvocation) {
        val injectHelper = AutoTrackHelper()
        val baseTransform = BaseTransform(transformInvocation, object : TransformCallBack {
             override fun process(className: String, classBytes: ByteArray?): ByteArray? {
                 if(AsmUtils.needHandle(className)){
                     return AsmUtils.handleTestClass3(classBytes!!)
                 }
                if (ClassUtils.checkClassName(className)) {
                    try {
                        return injectHelper.modifyClass(classBytes!!)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                return null
            }
        })
        baseTransform.startTransform()
    }


}