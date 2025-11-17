package tcs.app.dev.homework1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tcs.app.dev.R
import tcs.app.dev.homework1.data.Euro
import tcs.app.dev.homework1.data.Item
import tcs.app.dev.homework1.data.Shop
import tcs.app.dev.homework1.data.MockData

@Composable
fun StoreItemList(
    shop: Shop,
    modifier: Modifier = Modifier,
    onAddItem: (Item) -> Unit
) {
    val items = shop.items.sortedBy { item -> item.id }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            val price = shop.prices[item] ?: return@items
            StoreItemRow(
                item = item,
                price = price,
                onAddItem = onAddItem
            )
        }
    }
}

@Composable
private fun StoreItemRow(
    item: Item,
    price: Euro,
    onAddItem: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = MockData.getImage(item)),
                contentDescription = stringResource(id = MockData.getName(item)),
                modifier = Modifier.size(56.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = MockData.getName(item)),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = price.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = { onAddItem(item) }) {
                Text(text = stringResource(id = R.string.description_add_to_cart))
            }
        }
    }
}

