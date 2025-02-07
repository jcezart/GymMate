package com.example.gymmate


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Shape personalizado com a ponta apontando para o canto direito inferior
val speechBubbleShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // Dimensões para a ponta e os cantos
        val pointerWidth = 30f
        val pointerHeight = 15f
        val cornerRadius = 16f
        // Altura do corpo principal (deixa espaço para a ponta)
        val bubbleBottom = size.height - pointerHeight

        val path = Path()
        // Inicia no canto superior esquerdo, compensando o arredondamento
        path.moveTo(cornerRadius, 0f)
        // Linha superior até próximo do canto superior direito
        path.lineTo(size.width - cornerRadius, 0f)
        // Arco do canto superior direito
        path.arcTo(
            rect = Rect(
                left = size.width - 2 * cornerRadius,
                top = 0f,
                right = size.width,
                bottom = 2 * cornerRadius
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        // Linha vertical direita até o final do corpo principal
        path.lineTo(size.width, bubbleBottom)
        // Insere a ponta: do final do corpo principal, vai até o vértice no canto inferior direito...
        path.lineTo(size.width, size.height)             // Vértice da ponta (canto inferior direito)
        // ... e volta para a base da ponta no corpo principal
        path.lineTo(size.width - pointerWidth, bubbleBottom)
        // Linha inferior do corpo principal até o canto inferior esquerdo
        path.lineTo(cornerRadius, bubbleBottom)
        // Arco do canto inferior esquerdo
        path.arcTo(
            rect = Rect(
                left = 0f,
                top = bubbleBottom - 2 * cornerRadius,
                right = 2 * cornerRadius,
                bottom = bubbleBottom
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        // Linha vertical esquerda até próximo do canto superior esquerdo
        path.lineTo(0f, cornerRadius)
        // Arco do canto superior esquerdo
        path.arcTo(
            rect = Rect(
                left = 0f,
                top = 0f,
                right = 2 * cornerRadius,
                bottom = 2 * cornerRadius
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.close()
        return Outline.Generic(path)
    }
}

@Composable
fun CustomTooltip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                shape = speechBubbleShape
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.surface,
            fontSize = 12.sp
        )
    }
}
