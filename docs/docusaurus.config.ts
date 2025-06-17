import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const config: Config = {
  title: 'TriggerX Documentation',
  tagline: 'Trigger UI. On time. Every time.',
  favicon: 'img/triggerx-logo.png',

  // Future flags, see https://docusaurus.io/docs/api/docusaurus-config#future
  future: {
    v4: true, // Improve compatibility with the upcoming Docusaurus v4
  },

  // Set the production url of your site here
  url: 'https://labs.meticha.com',
  trailingSlash: false,
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/triggerx/docs',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'Meticha', // Usually your GitHub org/user name.
  projectName: 'triggerx', // Usually your repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/meticha/triggerx/tree/main/docs/docs',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    colorMode: {
          defaultMode: 'dark',
          respectPrefersColorScheme: false,
    },
    image: 'img/triggerx-banner.png',
    tableOfContents: {
          minHeadingLevel: 2,
          maxHeadingLevel: 5,
    },
    navbar: {
      title: 'TriggerX',
      logo: {
        alt: 'TriggerX Logo',
        src: 'img/triggerx-logo.png',
      },
      items: [
        {
          href: 'https://github.com/meticha/triggerx',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Community',
          items: [
            {
              label: 'Discord',
              href: 'https://discord.gg/p3SC9ZKzb6',
            },
            {
              label: 'LinkedIn',
              href: 'https://www.linkedin.com/company/meticha',
            },
            {
              label: 'X',
              href: 'https://x.com/metichahq',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'GitHub',
              href: 'https://github.com/meticha',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Meticha`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
