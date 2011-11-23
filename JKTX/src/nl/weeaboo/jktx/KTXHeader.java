/* JKTX
 * 
 * Copyright (c) 2011 Timon Bijlsma
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
   
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package nl.weeaboo.jktx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class KTXHeader {

	public static final int HEADER_LENGTH = 64;
	
	public static final byte[] FILE_IDENTIFIER = new byte[] {
		   (byte)0xAB, 0x4B, 0x54, 0x58, 0x20, 0x31, 0x31, (byte)0xBB, 0x0D, 0x0A, 0x1A, 0x0A
	};
	
	private ByteOrder byteOrder;
	private boolean byteOrderNative;
	private int glType;
	private int glTypeSize;
	private int glFormat;
	private int glInternalFormat;
	private int glBaseInternalFormat;
	private int pixelWidth;
	private int pixelHeight;
	private int pixelDepth;
	private int numberOfArrayElements;
	private int numberOfFaces;
	private int numberOfMipmapLevels;
	private int bytesOfKeyValueData;
	
	public KTXHeader() {		
	}
	
	//Functions
	public void read(InputStream in) throws KTXFormatException, IOException {
		ByteBuffer buf = ByteBuffer.allocate(HEADER_LENGTH);
		KTXUtil.readFully(in, buf);
		read(buf);
	}
	
	public void read(ByteBuffer buf) throws KTXFormatException {
		ByteOrder oldOrder = buf.order();
		try {
			read0(buf);
		} catch (BufferUnderflowException bue) {
			throw new KTXFormatException("Unexpected end of input", bue);
		} finally {
			buf.order(oldOrder);
		}
	}
	
	private void read0(ByteBuffer buf) throws KTXFormatException {
		buf.order(ByteOrder.nativeOrder());
		
		//Check file identifier
		byte[] magic = new byte[FILE_IDENTIFIER.length];
		buf.get(magic);
		if (!Arrays.equals(magic, FILE_IDENTIFIER)) {
			throw new KTXFormatException("Input doesn't start with KTX file identifier");
		}
		
		//Check endianness and, if necessary, flip the buffer's endianness
		int endianness = buf.getInt();
		if (endianness == 0x04030201) {
			//Endianness OK
			byteOrderNative = true;
		} else if (endianness == 0x01020304) {
			//Endianness Reversed
			byteOrderNative = false;
		} else {
			throw new KTXFormatException(String.format("Endianness field has an unexpected value: %08x", endianness));
		}
		
		byteOrder = buf.order();
		if (!byteOrderNative) {
			if (byteOrder == ByteOrder.BIG_ENDIAN) {
				byteOrder = ByteOrder.LITTLE_ENDIAN;
			} else {
				byteOrder = ByteOrder.BIG_ENDIAN;
			}
			buf.order(byteOrder);			
		}
		
		glType = buf.getInt();
		glTypeSize = buf.getInt();
		if (glTypeSize != 1 && glTypeSize != 2 && glTypeSize != 4) {
			throw new KTXFormatException("glTypeSize not supported: " + glTypeSize);
		}
		
		glFormat = buf.getInt();
		glInternalFormat = buf.getInt();
		glBaseInternalFormat = buf.getInt();
		pixelWidth = buf.getInt();
		pixelHeight = buf.getInt();
		pixelDepth = buf.getInt();
		numberOfArrayElements = buf.getInt();
		numberOfFaces = buf.getInt();
		numberOfMipmapLevels = buf.getInt();
		bytesOfKeyValueData = buf.getInt();
	}

	@Override
	public String toString() {
		return String.format("%s[glType=%d, glTypeSize=%d, glFormat=%d, glInternalFormat=%d, " +
				"glBaseInternalFormat=%d, pixelWidth=%d, pixelHeight=%d, pixelDepth=%d, " +
				"numberOfArrayElements=%d, numberOfFaces=%d, numberOfMipmapLevels=%d, " +
				"bytesOfKeyValueData=%d]", getClass().getSimpleName(), glType, glTypeSize, glFormat,
				glInternalFormat, glBaseInternalFormat, pixelWidth, pixelHeight, pixelDepth,
				numberOfArrayElements, numberOfFaces, numberOfMipmapLevels, bytesOfKeyValueData);
	}
	
	//Getters
	public ByteOrder getByteOrder() { return byteOrder; }
	public boolean isByteOrderNative() { return byteOrderNative; }
	public int getGLType() { return glType; }
	public int getGLTypeSize() { return glTypeSize; }
	public int getGLFormat() { return glFormat; }
	public int getGLInternalFormat() { return glInternalFormat; }
	public int getGLBaseInternalFormat() { return glBaseInternalFormat; }
	public int getPixelWidth() { return pixelWidth; }
	public int getPixelWidth(int i) { return Math.max(1, getPixelWidth() >> i); }
	public int getPixelHeight() { return pixelHeight; }
	public int getPixelHeight(int i) { return Math.max(1, getPixelHeight() >> i); }
	public int getPixelDepth() { return pixelDepth; }
	public int getPixelDepth(int i) { return Math.max(1, getPixelDepth() >> i); }
	public int getNumberOfArrayElements() { return numberOfArrayElements; }
	public int getNumberOfFaces() { return numberOfFaces; }
	public int getNumberOfMipmapLevels() { return numberOfMipmapLevels; }
	public int getBytesOfKeyValueData() { return bytesOfKeyValueData; }
	
}