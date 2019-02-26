//A
{
if (lenW) {
		unsigned int left = 64 - lenW;
		if (len < left)
			left = len;
		memcpy(lenW + (char *)ctx->W, data, left);
		lenW = (lenW + left) & 63;
		len -= left;
		data = ((const char *)data + left);
		if (lenW)
			return;
		blk_SHA1_Block(ctx, ctx->W);
	}

//B
	const char *base;

	if (path)
		skip_dos_drive_prefix(&path);

	if (!path || !*path)
		return "a";

	for (base = path; *path; path++) {
		if (!is_dir_sep(*path))
			continue;
		do {
			path++;
		} while (is_dir_sep(*path));
		if (*path)
			base = path;
		else
			while (--path != base && is_dir_sep(*path))
				*path = '\0';
	}
	return (char *)base;
}