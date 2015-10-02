Provides a simple parser for the KTX texture file format. The raw texture data is returned as a set of direct ByteBuffers ready to pass to an OpenGL binding like JOGL.

Some utility classes for converting images and (simple) DDS textures to KTX format are also included.

Unlike libktx, ETC1 textures are not decoded by the library if hardware decoding isn't available.

More information concerning the KTX format and libktx can be found at: http://www.khronos.org/opengles/sdk/tools/KTX/