import {
  type KeyboardEvent,
  type MouseEvent,
  type ReactNode,
  useCallback,
  useEffect,
  useRef,
  useState,
} from "react";
import { Link, useNavigate } from "react-router";
import { useSelectionFeedback } from "../../../hooks/useSelectionFeedback";
import type { SelectionFeedbackProps } from "../../../hooks/useSelectionFeedback";

import "./menu.css";

export type MenuItem = Readonly<{
  id: string;
  label: ReactNode;
  href?: string;
  onSelect?: () => void | Promise<void>;
  children?: readonly MenuItem[];
  disabled?: boolean;
}>;

export type MenuProps = Readonly<{
  items: readonly MenuItem[];
  className?: string;
  orientation?: "horizontal" | "vertical";
  onClose?: () => void;
}>;

type MenuLevelProps = Readonly<{
  items: readonly MenuItem[];
  level: number;
  parentKey: string;
  openItems: ReadonlySet<string>;
  getFeedbackProps: (itemKey: string) => SelectionFeedbackProps;
  onToggle: (itemKey: string) => void;
  onSelect: (
    item: MenuItem,
    itemKey: string,
    event?: MouseEvent<HTMLAnchorElement>,
  ) => void;
}>;

function itemHasChildren(item: MenuItem) {
  return Boolean(item.children?.length);
}

function MenuLevel({
  items,
  level,
  parentKey,
  openItems,
  getFeedbackProps,
  onToggle,
  onSelect,
}: MenuLevelProps) {
  return (
    <ul className="recursive-menu__list" data-level={level}>
      {items.map((item) => {
        const itemKey = parentKey ? `${parentKey}/${item.id}` : item.id;
        const hasChildren = itemHasChildren(item);
        const isOpen = openItems.has(itemKey);
        const feedback = getFeedbackProps(itemKey);
        const controlClassName = [
          "recursive-menu__control",
          feedback.className,
          hasChildren && "recursive-menu__trigger",
        ]
          .filter(Boolean)
          .join(" ");

        return (
          <li className="recursive-menu__item" key={item.id}>
            {hasChildren ? (
              <button
                aria-expanded={isOpen}
                className={controlClassName}
                disabled={item.disabled}
                onClick={() => onToggle(itemKey)}
                type="button">
                <span>{item.label}</span>
                <svg
                  aria-hidden="true"
                  className="recursive-menu__chevron"
                  fill="none"
                  focusable="false"
                  viewBox="0 0 20 20">
                  <path
                    d="m7.5 5 5 5-5 5"
                    stroke="currentColor"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="1.5" />
                </svg>
              </button>
            ) : item.href ? (
              <Link
                {...feedback}
                aria-disabled={item.disabled || undefined}
                className={controlClassName}
                onClick={(event) => onSelect(item, itemKey, event)}
                tabIndex={item.disabled ? -1 : undefined}
                to={item.href}>
                {item.label}
              </Link>
            ) : (
              <button
                {...feedback}
                className={controlClassName}
                disabled={item.disabled}
                onClick={() => onSelect(item, itemKey)}
                type="button">
                {item.label}
              </button>
            )}

            {hasChildren && isOpen ? (
              <MenuLevel
                items={item.children ?? []}
                level={level + 1}
                onSelect={onSelect}
                onToggle={onToggle}
                openItems={openItems}
                parentKey={itemKey}
                getFeedbackProps={getFeedbackProps}
              />
            ) : null}
          </li>
        );
      })}
    </ul>
  );
}

export default function Menu({
  items,
  className,
  orientation = "horizontal",
  onClose,
}: MenuProps) {
  const navigate = useNavigate();
  const [openItems, setOpenItems] = useState<ReadonlySet<string>>(new Set());
  const { getFeedbackProps, selectWithFeedback } = useSelectionFeedback();
  const rootRef = useRef<HTMLDivElement>(null);

  const closeMenu = useCallback(
    (notify = false) => {
      if (openItems.size > 0) {
        setOpenItems(new Set());
      }

      if (notify || openItems.size > 0) {
        onClose?.();
      }
    },
    [onClose, openItems.size],
  );

  useEffect(() => {
    const handlePointerDown = (event: PointerEvent) => {
      if (!rootRef.current?.contains(event.target as Node)) {
        closeMenu();
      }
    };

    const handleKeyDown = (event: globalThis.KeyboardEvent) => {
      if (event.key === "Escape") {
        closeMenu();
      }
    };

    document.addEventListener("pointerdown", handlePointerDown);
    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.removeEventListener("pointerdown", handlePointerDown);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [closeMenu]);

  const handleToggle = (itemKey: string) => {
    setOpenItems((currentOpenItems) => {
      const nextOpenItems = new Set(currentOpenItems);

      if (nextOpenItems.has(itemKey)) {
        for (const openItem of nextOpenItems) {
          if (openItem === itemKey || openItem.startsWith(`${itemKey}/`)) {
            nextOpenItems.delete(openItem);
          }
        }
      } else {
        const separatorIndex = itemKey.lastIndexOf("/");
        const parentPath =
          separatorIndex === -1 ? "" : itemKey.slice(0, separatorIndex);

        for (const openItem of nextOpenItems) {
          const openItemSeparatorIndex = openItem.lastIndexOf("/");
          const openItemParentPath =
            openItemSeparatorIndex === -1
              ? ""
              : openItem.slice(0, openItemSeparatorIndex);

          if (openItemParentPath === parentPath) {
            nextOpenItems.delete(openItem);

            for (const descendant of nextOpenItems) {
              if (descendant.startsWith(`${openItem}/`)) {
                nextOpenItems.delete(descendant);
              }
            }
          }
        }

        nextOpenItems.add(itemKey);
      }

      return nextOpenItems;
    });
  };

  const handleSelect = async (
    item: MenuItem,
    itemKey: string,
    event?: MouseEvent<HTMLAnchorElement>,
  ) => {
    if (item.disabled) {
      event?.preventDefault();
      return;
    }

    const shouldKeepNativeNavigation = Boolean(
      event &&
        (event.button !== 0 ||
          event.metaKey ||
          event.ctrlKey ||
          event.shiftKey ||
          event.altKey),
    );

    if (shouldKeepNativeNavigation) {
      return;
    }

    event?.preventDefault();

    await selectWithFeedback(itemKey, async () => {
      closeMenu(true);
      if (item.onSelect) {
        await item.onSelect();
      } else if (item.href) {
        await navigate(item.href);
      }
    });
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key === "Escape") {
      event.stopPropagation();
      closeMenu();
    }
  };

  const rootClassName = ["recursive-menu", className]
    .filter(Boolean)
    .join(" ");

  return (
    <div
      className={rootClassName}
      data-orientation={orientation}
      onKeyDown={handleKeyDown}
      ref={rootRef}>
      <MenuLevel
        items={items}
        level={0}
        onSelect={handleSelect}
        onToggle={handleToggle}
        openItems={openItems}
        parentKey=""
        getFeedbackProps={getFeedbackProps}
      />
    </div>
  );
}
