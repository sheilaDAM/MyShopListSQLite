package com.sheilajnieto.myshoplistsqlite.interfaces;

public interface IOnClickListener {
    void onShoppingListClicked(int position, int ListId);
    void onCategoryClicked(int position);
    void onProductClicked(int position);

}
