package io.github.petercrawley.livemap

@Suppress("unused")
internal data class Position2D<T : Number>(
	internal val x: T,
	internal val z: T
)