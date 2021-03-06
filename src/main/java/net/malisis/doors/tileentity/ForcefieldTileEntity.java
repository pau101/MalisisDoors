/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.doors.tileentity;

import net.malisis.core.block.IBoundingBox;
import net.malisis.core.util.AABBUtils;
import net.malisis.core.util.multiblock.AABBMultiBlock;
import net.malisis.core.util.syncer.Sync;
import net.malisis.core.util.syncer.Syncable;
import net.malisis.core.util.syncer.Syncer;
import net.malisis.doors.MalisisDoors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
@Syncable("TileEntity")
public class ForcefieldTileEntity extends TileEntity
{
	private AABBMultiBlock multiBlock;
	private boolean opened = false;

	public ForcefieldTileEntity()
	{

	}

	@Sync("opened")
	public boolean isOpened()
	{
		return opened;
	}

	@Sync("opened")
	public void setOpened(boolean opened)
	{
		this.opened = opened;
	}

	public void switchForcefield()
	{
		opened = !opened;
		Syncer.sync(this, "opened");
	}

	public void setMultiBlock(AABBMultiBlock multiBlock)
	{
		this.multiBlock = multiBlock;
	}

	public AABBMultiBlock getMultiBlock()
	{
		return multiBlock;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		opened = tag.getBoolean("opened");

		//LEGACY
		if (tag.hasKey("multiBlock"))
			tag = tag.getCompoundTag("multiBlock");

		if (tag.hasKey("minX"))
		{
			AxisAlignedBB aabb = AABBUtils.readFromNBT(tag);
			multiBlock = new AABBMultiBlock(MalisisDoors.Blocks.forcefieldDoor, aabb);
			multiBlock.setBulkProcess(true, true);
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		if (multiBlock != null)
			AABBUtils.writeToNBT(tag, multiBlock.getBoundingBox());

		tag.setBoolean("opened", opened);
		return tag;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return IBoundingBox.getRenderingBounds(world, pos);
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 1;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return oldState.getBlock() != newSate.getBlock();
	}

}
