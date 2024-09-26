

const killAllChildren = (element: HTMLElement) => {
    while (element.firstChild) {
        element.removeChild(element.firstChild);
      }
}

export {killAllChildren};