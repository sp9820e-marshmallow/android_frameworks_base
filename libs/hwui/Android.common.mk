# getConfig in external/skia/include/core/SkBitmap.h is deprecated.
# Allow Gnu extension: in-class initializer of static 'const float' member.
LOCAL_CLANG_CFLAGS += \
    -Wno-unused-parameter \
    -Wno-deprecated-declarations \
    -Wno-gnu-static-float-init

LOCAL_SRC_FILES := \
    utils/Blur.cpp \
    utils/GLUtils.cpp \
    utils/SortedListImpl.cpp \
    thread/TaskManager.cpp \
    font/CacheTexture.cpp \
    font/Font.cpp \
    AmbientShadow.cpp \
    AnimationContext.cpp \
    Animator.cpp \
    AnimatorManager.cpp \
    AssetAtlas.cpp \
    Caches.cpp \
    CanvasState.cpp \
    DamageAccumulator.cpp \
    DisplayList.cpp \
    DeferredDisplayList.cpp \
    DeferredLayerUpdater.cpp \
    DisplayListLogBuffer.cpp \
    DisplayListRenderer.cpp \
    Dither.cpp \
    DrawProfiler.cpp \
    Extensions.cpp \
    FboCache.cpp \
    FontRenderer.cpp \
    GammaFontRenderer.cpp \
    GradientCache.cpp \
    Image.cpp \
    Interpolator.cpp \
    Layer.cpp \
    LayerCache.cpp \
    LayerRenderer.cpp \
    Matrix.cpp \
    OpenGLRenderer.cpp \
    Patch.cpp \
    PatchCache.cpp \
    PathCache.cpp \
    PathTessellator.cpp \
    PixelBuffer.cpp \
    Program.cpp \
    ProgramCache.cpp \
    RenderBufferCache.cpp \
    RenderNode.cpp \
    RenderProperties.cpp \
    RenderState.cpp \
    ResourceCache.cpp \
    ShadowTessellator.cpp \
    SkiaShader.cpp \
    Snapshot.cpp \
    SpotShadow.cpp \
    Stencil.cpp \
    TessellationCache.cpp \
    Texture.cpp \
    TextureCache.cpp \
    TextDropShadowCache.cpp

# RenderThread stuff
LOCAL_SRC_FILES += \
    renderthread/CanvasContext.cpp \
    renderthread/DrawFrameTask.cpp \
    renderthread/EglManager.cpp \
    renderthread/RenderProxy.cpp \
    renderthread/RenderTask.cpp \
    renderthread/RenderThread.cpp \
    renderthread/TimeLord.cpp

intermediates := $(call intermediates-dir-for,STATIC_LIBRARIES,libRS,TARGET,)

LOCAL_C_INCLUDES += \
    external/skia/src/core

LOCAL_CFLAGS += -DEGL_EGLEXT_PROTOTYPES -DGL_GLEXT_PROTOTYPES
LOCAL_CFLAGS += -Wno-unused-parameter
LOCAL_SHARED_LIBRARIES := liblog libcutils libutils libEGL libGLESv2 libskia libui libgui

ifneq (false,$(ANDROID_ENABLE_RENDERSCRIPT))
    LOCAL_CFLAGS += -DANDROID_ENABLE_RENDERSCRIPT
    LOCAL_SHARED_LIBRARIES += libRS libRScpp
    LOCAL_C_INCLUDES += \
        $(intermediates) \
        frameworks/rs/cpp \
        frameworks/rs \

endif

ifndef HWUI_COMPILE_SYMBOLS
    LOCAL_CFLAGS += -fvisibility=hidden
endif

ifdef HWUI_COMPILE_FOR_PERF
    # TODO: Non-arm?
    LOCAL_CFLAGS += -fno-omit-frame-pointer -marm -mapcs
endif

# Defaults for ATRACE_TAG and LOG_TAG for libhwui
LOCAL_CFLAGS += -DATRACE_TAG=ATRACE_TAG_VIEW -DLOG_TAG=\"OpenGLRenderer\"

LOCAL_CFLAGS += -Wall -Werror -Wunused -Wunreachable-code